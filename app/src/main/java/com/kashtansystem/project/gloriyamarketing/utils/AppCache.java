package com.kashtansystem.project.gloriyamarketing.utils;

import com.kashtansystem.project.gloriyamarketing.models.template.UserDetial;
import com.kashtansystem.project.gloriyamarketing.models.template.UserTemplate;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 17.05.2017.
 * ----------------------------------
 */

public final class AppCache
{
    /** Данные авторизованного пользователя */
    public static UserTemplate USER_INFO = new UserTemplate();

    /** New feature, multi activity_agent in one*/
    public static ArrayList<UserDetial> USERS_DETAIL = new ArrayList<>();
    public static UserType userType = null;

}