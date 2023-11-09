package com.morewires.util;

import net.minecraft.util.math.Vec3d;

public class Color {
    public static int[] rgbIntToMassive(int RGBInt){
        int Red = RGBInt/256/256%256;
        int Green = RGBInt/256%256;
        int Blue = RGBInt%256;
        int[] RGB = new int[3];
        RGB[0] = Red;
        RGB[1] = Green;
        RGB[2] = Blue;
        return RGB;
    }

    public static Vec3d rgbIntToVec3D(int RGBInt){
        int Red = RGBInt/256/256%256;
        int Green = RGBInt/256%256;
        int Blue = RGBInt%256;
        return new Vec3d(Red,Green,Blue);
    }

    public static int rgbToRgbInt(Vec3d vec3d){
        int red = (int) vec3d.getX();
        int green = (int) vec3d.getY();
        int blue = (int) vec3d.getZ();
        return 256*256*red+256*green+blue;
    }

    public static int rgbToRgbInt(int[] Rgb){
        int red = Rgb[0];
        int green = Rgb[1];
        int blue = Rgb[2];
        return 256*256*red+256*green+blue;
    }
}
