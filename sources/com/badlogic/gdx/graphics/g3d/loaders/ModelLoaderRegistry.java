package com.badlogic.gdx.graphics.g3d.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelLoaderHints;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dLoader;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dtLoader;
import com.badlogic.gdx.graphics.g3d.loaders.md2.MD2Loader;
import com.badlogic.gdx.graphics.g3d.loaders.wavefront.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.Model;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedModel;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.HashMap;
import java.util.Map;

public class ModelLoaderRegistry {
    private static Map<String, Array<ModelLoaderHints>> defaultHints = new HashMap();
    private static Map<String, Array<ModelLoader>> loaders = new HashMap();

    static {
        registerLoader("obj", new ObjLoader(), new ModelLoaderHints(false));
        registerLoader("md2", new MD2Loader(), new MD2Loader.MD2LoaderHints(0.2f));
        registerLoader("g3dt", new G3dtLoader.G3dtStillModelLoader(), new ModelLoaderHints(true));
        registerLoader("g3dt", new G3dtLoader.G3dtKeyframedModelLoader(), new ModelLoaderHints(true));
        registerLoader("g3d", new G3dLoader.G3dStillModelLoader(), new ModelLoaderHints(false));
        registerLoader("g3d", new G3dLoader.G3dKeyframedModelLoader(), new ModelLoaderHints(false));
        registerLoader("g3d", new G3dLoader.G3dSkeletonModelLoader(), new ModelLoaderHints(false));
    }

    public static void registerLoader(String extension, ModelLoader loader, ModelLoaderHints defaultHints2) {
        Array<ModelLoader> loaders2 = loaders.get(extension);
        if (loaders2 == null) {
            loaders2 = new Array<>();
            loaders.put(extension.toLowerCase(), loaders2);
        }
        loaders2.add(loader);
        Array<ModelLoaderHints> hints = defaultHints.get(extension);
        if (hints == null) {
            hints = new Array<>();
            defaultHints.put(extension.toLowerCase(), hints);
        }
        hints.add(defaultHints2);
    }

    public static Model load(FileHandle file) {
        String name = file.name();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            throw new GdxRuntimeException("file '" + file.name() + "' does not have an extension that can be matched to a ModelLoader");
        }
        String extension = name.substring(dotIndex + 1).toLowerCase();
        Array<ModelLoader> loaders2 = loaders.get(extension);
        Array<ModelLoaderHints> hints = defaultHints.get(extension);
        if (loaders2 == null) {
            throw new GdxRuntimeException("no loaders for extension '" + extension + "'");
        } else if (hints == null) {
            throw new GdxRuntimeException("no default hints for extension '" + extension + "'");
        } else {
            Model model = null;
            StringBuilder errors = new StringBuilder();
            for (int i = 0; i < loaders2.size; i++) {
                ModelLoader loader = loaders2.get(i);
                try {
                    model = loader.load(file, hints.get(i));
                } catch (GdxRuntimeException e) {
                    errors.append("Couldn't load '" + file.name() + "' with loader of type " + loader.getClass().getName() + ": " + e.getMessage() + "\n");
                }
            }
            if (model != null) {
                return model;
            }
            throw new GdxRuntimeException(errors.toString());
        }
    }

    public static Model load(FileHandle file, ModelLoaderHints hints) {
        String name = file.name();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            throw new GdxRuntimeException("file '" + file.name() + "' does not have an extension that can be matched to a ModelLoader");
        }
        String extension = name.substring(dotIndex + 1).toLowerCase();
        Array<ModelLoader> loaders2 = loaders.get(extension);
        if (loaders2 == null) {
            throw new GdxRuntimeException("no loaders for extension '" + extension + "'");
        }
        Model model = null;
        StringBuilder errors = new StringBuilder();
        for (int i = 0; i < loaders2.size; i++) {
            ModelLoader loader = loaders2.get(i);
            try {
                model = loader.load(file, hints);
            } catch (GdxRuntimeException e) {
                errors.append("Couldn't load '" + file.name() + "' with loader of type " + loader.getClass().getName() + ": " + e.getMessage() + "\n");
            }
        }
        if (model != null) {
            return model;
        }
        throw new GdxRuntimeException(errors.toString());
    }

    public static StillModel loadStillModel(FileHandle file) {
        String name = file.name();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            throw new GdxRuntimeException("file '" + file.name() + "' does not have an extension that can be matched to a ModelLoader");
        }
        String extension = name.substring(dotIndex + 1).toLowerCase();
        Array<ModelLoader> loaders2 = loaders.get(extension);
        Array<ModelLoaderHints> hints = defaultHints.get(extension);
        if (loaders2 == null) {
            throw new GdxRuntimeException("no loaders for extension '" + extension + "'");
        } else if (hints == null) {
            throw new GdxRuntimeException("no default hints for extension '" + extension + "'");
        } else {
            StillModel model = null;
            StringBuilder errors = new StringBuilder();
            for (int i = 0; i < loaders2.size; i++) {
                ModelLoader loader = loaders2.get(i);
                ModelLoaderHints hint = hints.get(i);
                try {
                    if (loader instanceof StillModelLoader) {
                        model = ((StillModelLoader) loader).load(file, hint);
                    }
                } catch (GdxRuntimeException e) {
                    errors.append("Couldn't load '" + file.name() + "' with loader of type " + loader.getClass().getName() + ": " + e.getMessage() + "\n");
                }
            }
            if (model != null) {
                return model;
            }
            throw new GdxRuntimeException("Couldn't load model '" + file.name() + "', " + errors.toString());
        }
    }

    public static StillModel loadStillModel(FileHandle file, ModelLoaderHints hints) {
        String name = file.name();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            throw new GdxRuntimeException("file '" + file.name() + "' does not have an extension that can be matched to a ModelLoader");
        }
        String extension = name.substring(dotIndex + 1).toLowerCase();
        Array<ModelLoader> loaders2 = loaders.get(extension);
        if (loaders2 == null) {
            throw new GdxRuntimeException("no loaders for extension '" + extension + "'");
        }
        StillModel model = null;
        StringBuilder errors = new StringBuilder();
        for (int i = 0; i < loaders2.size; i++) {
            ModelLoader loader = loaders2.get(i);
            try {
                if (loader instanceof StillModelLoader) {
                    model = ((StillModelLoader) loader).load(file, hints);
                }
            } catch (GdxRuntimeException e) {
                errors.append("Couldn't load '" + file.name() + "' with loader of type " + loader.getClass().getName() + ": " + e.getMessage() + "\n");
            }
        }
        if (model != null) {
            return model;
        }
        throw new GdxRuntimeException("Couldn't load model '" + file.name() + "', " + errors.toString());
    }

    public static KeyframedModel loadKeyframedModel(FileHandle file) {
        String name = file.name();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            throw new GdxRuntimeException("file '" + file.name() + "' does not have an extension that can be matched to a ModelLoader");
        }
        String extension = name.substring(dotIndex + 1).toLowerCase();
        Array<ModelLoader> loaders2 = loaders.get(extension);
        Array<ModelLoaderHints> hints = defaultHints.get(extension);
        if (loaders2 == null) {
            throw new GdxRuntimeException("no loaders for extension '" + extension + "'");
        } else if (hints == null) {
            throw new GdxRuntimeException("no default hints for extension '" + extension + "'");
        } else {
            KeyframedModel model = null;
            StringBuilder errors = new StringBuilder();
            for (int i = 0; i < loaders2.size; i++) {
                ModelLoader loader = loaders2.get(i);
                ModelLoaderHints hint = hints.get(i);
                try {
                    if (loader instanceof KeyframedModelLoader) {
                        model = ((KeyframedModelLoader) loader).load(file, hint);
                    }
                } catch (GdxRuntimeException e) {
                    errors.append("Couldn't load '" + file.name() + "' with loader of type " + loader.getClass().getName() + ": " + e.getMessage() + "\n");
                }
            }
            if (model != null) {
                return model;
            }
            throw new GdxRuntimeException(errors.toString());
        }
    }

    public static KeyframedModel loadKeyframedModel(FileHandle file, ModelLoaderHints hints) {
        String name = file.name();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            throw new GdxRuntimeException("file '" + file.name() + "' does not have an extension that can be matched to a ModelLoader");
        }
        String extension = name.substring(dotIndex + 1).toLowerCase();
        Array<ModelLoader> loaders2 = loaders.get(extension);
        if (loaders2 == null) {
            throw new GdxRuntimeException("no loaders for extension '" + extension + "'");
        }
        KeyframedModel model = null;
        StringBuilder errors = new StringBuilder();
        for (int i = 0; i < loaders2.size; i++) {
            ModelLoader loader = loaders2.get(i);
            try {
                if (loader instanceof KeyframedModelLoader) {
                    model = ((KeyframedModelLoader) loader).load(file, hints);
                }
            } catch (GdxRuntimeException e) {
                errors.append("Couldn't load '" + file.name() + "' with loader of type " + loader.getClass().getName() + ": " + e.getMessage() + "\n");
            }
        }
        if (model != null) {
            return model;
        }
        throw new GdxRuntimeException(errors.toString());
    }

    public static SkeletonModel loadSkeletonModel(FileHandle file) {
        String name = file.name();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            throw new GdxRuntimeException("file '" + file.name() + "' does not have an extension that can be matched to a ModelLoader");
        }
        String extension = name.substring(dotIndex + 1).toLowerCase();
        Array<ModelLoader> loaders2 = loaders.get(extension);
        Array<ModelLoaderHints> hints = defaultHints.get(extension);
        if (loaders2 == null) {
            throw new GdxRuntimeException("no loaders for extension '" + extension + "'");
        } else if (hints == null) {
            throw new GdxRuntimeException("no default hints for extension '" + extension + "'");
        } else {
            SkeletonModel model = null;
            StringBuilder errors = new StringBuilder();
            for (int i = 0; i < loaders2.size; i++) {
                ModelLoader loader = loaders2.get(i);
                ModelLoaderHints hint = hints.get(i);
                try {
                    if (loader instanceof SkeletonModelLoader) {
                        model = ((SkeletonModelLoader) loader).load(file, hint);
                    }
                } catch (GdxRuntimeException e) {
                    errors.append("Couldn't load '" + file.name() + "' with loader of type " + loader.getClass().getName() + ": " + e.getMessage());
                }
            }
            if (model != null) {
                return model;
            }
            throw new GdxRuntimeException(errors.toString());
        }
    }

    public static SkeletonModel loadSkeletonModel(FileHandle file, ModelLoaderHints hints) {
        String name = file.name();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            throw new GdxRuntimeException("file '" + file.name() + "' does not have an extension that can be matched to a ModelLoader");
        }
        String extension = name.substring(dotIndex + 1).toLowerCase();
        Array<ModelLoader> loaders2 = loaders.get(extension);
        if (loaders2 == null) {
            throw new GdxRuntimeException("no loaders for extension '" + extension + "'");
        }
        SkeletonModel model = null;
        StringBuilder errors = new StringBuilder();
        for (int i = 0; i < loaders2.size; i++) {
            ModelLoader loader = loaders2.get(i);
            try {
                if (loader instanceof SkeletonModelLoader) {
                    model = ((SkeletonModelLoader) loader).load(file, hints);
                }
            } catch (GdxRuntimeException e) {
                errors.append("Couldn't load '" + file.name() + "' with loader of type " + loader.getClass().getName() + ": " + e.getMessage());
            }
        }
        if (model != null) {
            return model;
        }
        throw new GdxRuntimeException(errors.toString());
    }
}
