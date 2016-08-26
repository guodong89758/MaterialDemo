package com.guo.material.log;



public class UMPageFilter
{
    final static String[] pagearray = new String[]
    {
        "BaseView",
    // MainView.TAG,
    // SwipeBackView.TAG,
    // GuildView.TAG,
    // SquareView.TAG,
    // GiftbagView.TAG,
    };

    /**
     * @param pagename
     * @return true 有效页面埋点 false 无效页面
     */
    public static boolean allow(String pagename)
    {
        if (pagename == null || pagename.length() == 0)
            return false;
        for (String p : pagearray)
        {
            if (p.equals(pagename))
                return false;
        }
        return true;
    }
}
