package com.badlogic.gdx.utils;

import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;

public class GdxBuild {
    public static void main(String[] args) throws Exception {
        new NativeCodeGenerator().generate("src", "bin", "jni", new String[]{"**/*"}, (String[]) null);
        BuildTarget win32home = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
        win32home.compilerPrefix = "";
        win32home.buildFileName = "build-windows32home.xml";
        win32home.excludeFromMasterBuildFile = true;
        BuildTarget win32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
        BuildTarget win64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, true);
        BuildTarget lin32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, false);
        BuildTarget lin64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, true);
        BuildTarget android = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Android, false);
        BuildTarget mac = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, false);
        new AntScriptGenerator().generate(new BuildConfig("gdx", "../target/native", "libs", "jni"), new BuildTarget[]{mac, win32home, win32, win64, lin32, lin64, android});
    }
}
