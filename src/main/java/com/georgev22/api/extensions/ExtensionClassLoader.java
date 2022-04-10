package com.georgev22.api.extensions;

import com.georgev22.api.maps.ConcurrentObjectMap;
import com.georgev22.api.maps.ObjectMap;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * A ClassLoader for extensions, to allow shared classes across multiple extensions
 */
public final class ExtensionClassLoader extends URLClassLoader {
    private final ObjectMap<String, Class<?>> classes = new ConcurrentObjectMap<>();
    private final File dataFolder;
    private final File file;
    private final ExtensionDescriptionFile extensionDescriptionFile;
    private final JarFile jar;
    final Extension extension;
    private Extension extensionInit;
    private IllegalStateException extensionState;
    private final Logger logger;
    private final Set<String> seenIllegalAccess = Collections.newSetFromMap(new ConcurrentObjectMap<>());

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public ExtensionClassLoader(@Nullable final ClassLoader parent, @NotNull ExtensionDescriptionFile extensionDescriptionFile, @NotNull final File dataFolder, @NotNull final File file, @NotNull Logger logger) throws Exception {
        super(new URL[]{file.toURI().toURL()}, parent);
        this.dataFolder = dataFolder;
        this.file = file;
        this.jar = new JarFile(file);
        this.extensionDescriptionFile = extensionDescriptionFile;
        this.logger = logger;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(extensionDescriptionFile.getMain(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new Exception("Cannot find main class `" + extensionDescriptionFile.getMain() + "'", ex);
            }

            Class<? extends Extension> extensionClass;
            try {
                extensionClass = jarClass.asSubclass(Extension.class);
            } catch (ClassCastException ex) {

                throw new Exception("main class `" + extensionDescriptionFile.getMain() + "' does not extend " + Extension.class.getName() + " (" + jarClass.getSuperclass().getName() + ")", ex);
            }

            extension = extensionClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new Exception("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new Exception("Abnormal extension type", ex);
        }
    }

    @Override
    public URL getResource(String name) {
        return findResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return findResources(name);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            jar.close();
        }
    }

    @NotNull
    Collection<Class<?>> getClasses() {
        return classes.values();
    }

    synchronized void initialize(@NotNull Extension extension) {
        Validate.notNull(extension, "Initializing extension cannot be null");
        Validate.isTrue(extension.getClass().getClassLoader() == this, "Cannot initialize extension outside of this class loader");
        if (this.extension != null || this.extensionInit != null) {
            throw new IllegalArgumentException("Extension already initialized!", extensionState);
        }

        extensionState = new IllegalStateException("Initial initialization");
        this.extensionInit = extension;

        extension.init(dataFolder, extensionDescriptionFile, file, this, logger);
    }
}
