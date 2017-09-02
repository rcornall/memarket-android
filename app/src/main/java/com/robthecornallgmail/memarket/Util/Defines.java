package com.robthecornallgmail.memarket.Util;

import android.view.animation.AlphaAnimation;

/**
 * Created by rob on 22/01/17.
 */

public final class Defines
{
    public static String SERVER_ADDRESS = "http://165.227.11.51:63420";
    public static AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    public static final class ITEM_TYPE {

        public static int CLOTHING = 1;
        public static int BUILDING = 2;
        public static int WORKER = 3;

        public static final class CLOTHING_TYPE {
            public static int HAT = 1;
            public static int GLASSES = 2;
            public static int NECKLACE = 3;
            public static int SHIRT = 4;
            public static int JACKET = 5;
            public static int GLOVES = 6;
            public static int PANTS = 7;
            public static int SHOES = 8;
        }

        public static final class OFFICE_TYPE {
            public static int SHORT_OFFICE_GREY = 1;
            public static int TALL_OFFICE_GREY = 2;
            public static int HOUSE_SMALL_SOMETHING = 3;
        }


        public static int EQUIPPED = 1;
        public static int NOT_EQUIPPED = 0;
    }

    public static final class DEFAULT_TYPE {
        public static int BACKGROUND = 0;
        public static int GROUND = 1;
        public static int CLOUD1 = 2;
        public static int GUY = 3;
        public static int INSIDE_BUILDING = 4;
        public static int INSIDE_BUILDING_TOP = 5;

    }
}

