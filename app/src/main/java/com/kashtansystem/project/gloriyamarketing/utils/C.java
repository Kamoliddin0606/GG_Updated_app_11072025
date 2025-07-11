package com.kashtansystem.project.gloriyamarketing.utils;

/**
 * Created by FlameKaf(GOVNO) on 10.05.2017.
 * ----------------------------------
 * Константы приложения.
 */

public final class C
{
    public interface SOAP
    {
        String NAME_SPACE = "http://www.sample-package.org";
        //String URL = "http://kit.gloriya.uz:5443/MobileAgents/MobileAgents.1cws";
        //String URL = "http://kit.gloriya.uz:5443/MobileAgentsTest/MobileAgentsTest.1cws";
        //String URL = "http://kit.gloriya.uz:5443/EVYAP_UT/EVYAP_UT.1cws";

        //String URL = "http://kit.gloriya.uz:5443/EVYAP_TEST/EVYAP_TEST.1cws";

        /*String Projects[] = {"EVYAP_TEST", "local_project"};
        String URLs[] = {"http://kit.gloriya.uz:5443/EVYAP_TEST/EVYAP_TEST.1cws",
                "http://10.0.2.2/gloriya/ws/MobileAgents"};*/
        String Projects[] = {
                "EVYAP",
                "GARNIER",
                "AVON",
                "PPD",
                "AVON Test",
                "AVON Test2",

        };
        String URLs[] = {
//                "http://kit.gloriya.uz:5443/VICHY_UT/VICHY_UT.1cws"
                "http://kit.gloriya.uz:5443/EVYAP_UT/EVYAP_UT.1cws",
                "http://kit.gloriya.uz:5443/loreal_ut/loreal_ut.1cws",
                "http://kit.gloriya.uz:5443/AVON_UT/AVON_UT.1cws",


                "http://kit.gloriya.uz:5443/UT_Professionnel/UT_Professionnel.1cws",
//                "http://192.168.0.129/AVON_Smartpost/AVON_Smartpost.1cws",
                "http://kit.gloriya.uz:5443/AVON_TEST/AVON_TEST.1cws",
                "http://192.168.1.189:5443/AVON_TEST/AVON_TEST.1cws",
//                "http://kit.gloriya.uz:5443/himalaya_ut/himalaya_ut.1cws",
//                "http://kit.gloriya.uz:5443/ut_test/ut_test.1cws"
        };

    }

    public interface REQUEST_CODES
    {
        int CREATE_ORDER = 100;
        int EDIT_ORDER = 101;
        int CAPTURE_PICTURE = 102;
        int GET_ANOTHER_GOODS = 103;
        int GET_CHOSEN_PRODUCT_RES = 104;
        int CREATE_NEW_CLIENT = 105;
        int TAKE_CASH = 106;
    }

    public interface KEYS
    {
        /* любые данные */
        String EXTRA_DATA = "com.kashtansystem.project.gloriyamarketing.keys.extra_data";
        /* ид заказа */
        String EXTRA_DATA_ID = "com.kashtansystem.project.gloriyamarketing.keys.extra_data_id";
        /* код тип оплаты */
        String EXTRA_DATA_PT = "com.kashtansystem.project.gloriyamarketing.keys.extra_data_pt";
        /* торговая точка */
        String EXTRA_DATA_TP = "com.kashtansystem.project.gloriyamarketing.keys.extra_data_tp";
        String EXTRA_DATA_CT = "com.kashtansystem.project.gloriyamarketing.keys.extra_data_ct";
        String EXTRA_DATA_W = "com.kashtansystem.project.gloriyamarketing.keys.warehouse";
        /* заказ */
        String EXTRA_DATA_ORDER = "com.kashtansystem.project.gloriyamarketing.keys.extra_data_order";
        /* можно ли изменять кол-во заказов */
        String EXTRA_DATA_CAN_CHANGE = "com.kashtansystem.project.gloriyamarketing.keys.extra_data_can_change";
        String EXTRA_LOC = "com.kashtansystem.project.gloriyamarketing.keys.extra_loc";
        String EXTRA_PATH = "com.kashtansystem.project.gloriyamarketing.keys.extra_path";
        String EXTRA_CONTENT = "com.kashtansystem.project.gloriyamarketing.keys.extra_content";
        String EXTRA_RES_DATA_DOUBLE = "com.kashtansystem.project.gloriyamarketing.keys.extra_res_data_double";
        String EXTRA_RES_DATA_BOOL = "com.kashtansystem.project.gloriyamarketing.keys.extra_res_data_bool";

        String SYNC_FREQUENCY_PREF_REGIONS = "sync_frequency_pref_regions";
        String SYNC_FREQUENCY_PREF_CLIENTS = "sync_frequency_pref_clients";
        String SYNC_FREQUENCY_PREF_PT = "sync_frequency_pref_price_types";

        String USER_CODE = "user_code";
    }

    public interface ACTIONS
    {
        String NOTIF_ABOUT_VISIT = "com.kashtansystem.project.gloriyamarketing.action.notif_about_visit";
        String FORWARDER_TAKE_CASH = "com.kashtansystem.project.gloriyamarketing.action.forwarder_take_cash";
        String COLLECTOR_TAKE_CASH = "com.kashtansystem.project.gloriyamarketing.action.collector_take_cash";

        String ACTION_VIEW_FORWARDER_LOCATION =
            "com.kashtansystem.project.gloriyamarketing.actions.action_view_forwarder_location";
        String ACTION_SHOW_TP_LOCATION =
            "com.kashtansystem.project.gloriyamarketing.actions.action_show_tp_location";
        String ACTION_GET_ADDRESS =
            "com.kashtansystem.project.gloriyamarketing.actions.action_get_address";
        String ACTION_CREATE_ORDER =
            "com.kashtansystem.project.gloriyamarketing.actions.action_create_order";
        String ACTION_EDIT_ORDER =
            "com.kashtansystem.project.gloriyamarketing.actions.action_edit_order";
        String ACTION_RELOAD_PRODUCTS =
            "com.kashtansystem.project.gloriyamarketing.actions.action_reload_products";
        String ACTION_RESEND_ORDERS =
            "com.kashtansystem.project.gloriyamarketing.actions.action_resend_orders";
    }
}