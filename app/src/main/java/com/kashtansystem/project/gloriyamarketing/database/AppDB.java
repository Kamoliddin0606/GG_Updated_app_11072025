package com.kashtansystem.project.gloriyamarketing.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.AgentActivity;
import com.kashtansystem.project.gloriyamarketing.database.tables.BusinessRegions;
import com.kashtansystem.project.gloriyamarketing.database.tables.Clients;
import com.kashtansystem.project.gloriyamarketing.database.tables.ContractType;
import com.kashtansystem.project.gloriyamarketing.database.tables.Contracts;
import com.kashtansystem.project.gloriyamarketing.database.tables.CreditVisits;
import com.kashtansystem.project.gloriyamarketing.database.tables.Dealers;
import com.kashtansystem.project.gloriyamarketing.database.tables.DistrictCity;
import com.kashtansystem.project.gloriyamarketing.database.tables.OrdersCompetitorScouting;
import com.kashtansystem.project.gloriyamarketing.database.tables.OrdersGiftList;
import com.kashtansystem.project.gloriyamarketing.database.tables.OrdersGoods;
import com.kashtansystem.project.gloriyamarketing.database.tables.OrdersNotEnoughGoodsByBrand;
import com.kashtansystem.project.gloriyamarketing.database.tables.OrdersNotEnoughGoodsBySeries;
import com.kashtansystem.project.gloriyamarketing.database.tables.OrdersNotEnoughGoods;
import com.kashtansystem.project.gloriyamarketing.database.tables.Orders;
import com.kashtansystem.project.gloriyamarketing.database.tables.Organization;
import com.kashtansystem.project.gloriyamarketing.database.tables.PS_PriceListUpdateTime;
import com.kashtansystem.project.gloriyamarketing.database.tables.ParcelTemp;
import com.kashtansystem.project.gloriyamarketing.database.tables.PriceList;
import com.kashtansystem.project.gloriyamarketing.database.tables.PriceType;
import com.kashtansystem.project.gloriyamarketing.database.tables.Products;
import com.kashtansystem.project.gloriyamarketing.database.tables.GiftList;
import com.kashtansystem.project.gloriyamarketing.database.tables.Refusals;
import com.kashtansystem.project.gloriyamarketing.database.tables.Stock;
import com.kashtansystem.project.gloriyamarketing.database.tables.LastSellDate;
import com.kashtansystem.project.gloriyamarketing.models.template.BusinessRegionsTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTypeTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.CreditVisitTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.OrganizationTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PS_CitiesDistrictContract;
import com.kashtansystem.project.gloriyamarketing.models.template.ParcelTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PriceTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ProductTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.CompetitorScoutingTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.OrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PriceTypeTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.RefusalTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.SeriesTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.WarehouseTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.LastSellDateTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.PS_ReqGetPriceListUpdateTime;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqPriceType;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.OrderStatus;
import com.kashtansystem.project.gloriyamarketing.utils.Util;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

/**
 * Created by FlameKaf on 05.05.2017.
 * ----------------------------------
 * База данных приложения. Хранит в основном временные данные по заказам.
 */

public class AppDB {
    private static AppDB instance;
    // @author MrJ
    private static SharedPreferences sp = null;
    //
    private static SQLiteDatabase sqLiteDatabase;

    private static class AppDBHelper extends SQLiteAssetHelper {
        private static final String dbName = "GloriyaMarketing";
        private static final int dbVersion = 9;

        AppDBHelper(Context context) {
            super(context, dbName, null, dbVersion);
        }
    }

    private AppDB(Context context) {
        sqLiteDatabase = new AppDBHelper(context).getWritableDatabase();
//        sqLiteDatabase.create()
        // @author MrJ
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        //
    }

    public static AppDB getInstance(Context context) {
        if (instance == null)
            instance = new AppDB(context);
        return instance;
    }

    /**
     * Сохраняет данные клиентов на устройстве
     *
     * @param items массив данных по клиентам
     */
    public synchronized void saveClients(LinkedList<TradingPointTemplate> items) {
        clearClients();
        final String current_date = System.currentTimeMillis() + "";

        try {
            sqLiteDatabase.beginTransaction();

            for (TradingPointTemplate client : items) {
                ContentValues fields = new ContentValues();
                fields.put(Clients.Code, client.getTpCode());
                fields.put(Clients.Name, client.getTitle());
                fields.put(Clients.Signboard, client.getSignboard());
                fields.put(Clients.Inn, client.getInn());
                fields.put(Clients.TradePointType, client.getTpType());
                fields.put(Clients.ContactPerson, client.getContactPerson());
                fields.put(Clients.ContactPersonPhone, client.getContactPersonPhone());
                fields.put(Clients.LegalAddress, "");
                fields.put(Clients.DeliveryAddress, client.getAddress());
                fields.put(Clients.ReferencePoint, client.getReferencePoint());
                fields.put(Clients.ResponsiblePersonPhone, client.getRespPersonPhone());
                fields.put(Clients.Latitude, client.getLatitude());
                fields.put(Clients.Longitude, client.getLongitude());
                fields.put(Clients.CreditLimit, client.getCreditLimit());
                fields.put(Clients.AccumulatedCredit, client.getAccumulatedCredit());
                fields.put(Clients.UserCode, "");
                fields.put(Clients.UpdatedDate, current_date);
                sqLiteDatabase.insertWithOnConflict(Clients.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception(">>> SAVE CLIENTS <<<");
            ex.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Сохраняет данные клиента на устройстве
     *
     * @param data Введённые данные по новому клиенту
     */
    public synchronized void saveClient(TradingPointTemplate data) {
        try {
            sqLiteDatabase.beginTransaction();
            ContentValues fields = new ContentValues();
            fields.put(Clients.Code, data.getTpCode());
            fields.put(Clients.Name, data.getTitle());
            fields.put(Clients.Signboard, data.getSignboard());
            fields.put(Clients.Inn, data.getInn());
            fields.put(Clients.TradePointType, data.getTpType());
            fields.put(Clients.ContactPerson, data.getContactPerson());
            fields.put(Clients.ContactPersonPhone, data.getContactPersonPhone());
            fields.put(Clients.LegalAddress, data.getLegalAddress());
            fields.put(Clients.DeliveryAddress, data.getAddress());
            fields.put(Clients.ReferencePoint, data.getReferencePoint());
            fields.put(Clients.ResponsiblePersonPhone, data.getRespPersonPhone());
            fields.put(Clients.Latitude, data.getLatitude() + "");
            fields.put(Clients.Longitude, data.getLongitude() + "");
            fields.put(Clients.CreditLimit, data.getCreditLimit() + "");
            fields.put(Clients.AccumulatedCredit, data.getAccumulatedCredit() + "");
            fields.put(Clients.UserCode, AppCache.USER_INFO.getUserCode());
            fields.put(Clients.UpdatedDate, System.currentTimeMillis() + "");
            sqLiteDatabase.insertWithOnConflict(Clients.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception(">>> SAVE CLIENTS <<<");
            ex.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Обновление информации по накопленному кредиту
     *
     * @param tpCode код торговой точки
     * @param credit накопленный кредит
     */
    public synchronized void updateClientCredit(String tpCode, Double credit) {
        sqLiteDatabase.beginTransaction();
        try {
            ContentValues cvFields = new ContentValues();
            cvFields.put(Clients.AccumulatedCredit, credit + "");
            sqLiteDatabase.updateWithOnConflict(Clients.Table, cvFields, String.format("%s = '%s'",
                    Clients.Code, tpCode), null, SQLiteDatabase.CONFLICT_REPLACE);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Проверяет нужно ли обновлять список клиентов
     *
     * @param days количество дней по истичению которых нужно обновить справочник
     */
    public synchronized boolean needToUpdateClients(int days) {
        // TODO: Необходимо учесть, что перезаходят разные пользователи с разными настройками
        if (days == -1)
            return false;

        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select %s from %s limit 1",
                Clients.UpdatedDate, Clients.Table), null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                long last_day = System.currentTimeMillis() - Long.parseLong(cursor.getString(0));
                return (TimeUnit.DAYS.convert(last_day, TimeUnit.MILLISECONDS) >= days);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return true;
    }

    public synchronized LinkedList<TradingPointTemplate> getClientsList() {
        LinkedList<TradingPointTemplate> result = new LinkedList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", Clients.Table), null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TradingPointTemplate client = new TradingPointTemplate();
                    client.setTpCode(cursor.getString(cursor.getColumnIndex(Clients.Code)));
                    client.setTpType(cursor.getString(cursor.getColumnIndex(Clients.TradePointType)));
                    client.setTitle(cursor.getString(cursor.getColumnIndex(Clients.Name)));
                    client.setInn(cursor.getString(cursor.getColumnIndex(Clients.Inn)));
                    client.setAddress(cursor.getString(cursor.getColumnIndex(Clients.DeliveryAddress)));
                    client.setSignboard(cursor.getString(cursor.getColumnIndex(Clients.Signboard)));
                    client.setReferencePoint(cursor.getString(cursor.getColumnIndex(Clients.ReferencePoint)));
                    client.setContactPerson(cursor.getString(cursor.getColumnIndex(Clients.ContactPerson)));
                    client.setContactPersonPhone(cursor.getString(cursor.getColumnIndex(Clients.ContactPersonPhone)));
                    client.setCreditLimit(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Clients.CreditLimit))));
                    client.setAcumulatedCredit(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Clients.AccumulatedCredit))));
                    client.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Clients.Latitude))));
                    client.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Clients.Longitude))));
                    result.add(client);
                }
                while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return result;
    }

    public synchronized TradingPointTemplate getClient(String tpCode) {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = '%s'",
                Clients.Table, Clients.Code, tpCode), null);
        try {
            if (cursor.moveToFirst()) {
                TradingPointTemplate client = new TradingPointTemplate();
                client.setTpCode(cursor.getString(cursor.getColumnIndex(Clients.Code)));
                client.setTpType(cursor.getString(cursor.getColumnIndex(Clients.TradePointType)));
                client.setTitle(cursor.getString(cursor.getColumnIndex(Clients.Name)));
                client.setInn(cursor.getString(cursor.getColumnIndex(Clients.Inn)));
                client.setAddress(cursor.getString(cursor.getColumnIndex(Clients.DeliveryAddress)));
                client.setSignboard(cursor.getString(cursor.getColumnIndex(Clients.Signboard)));
                client.setReferencePoint(cursor.getString(cursor.getColumnIndex(Clients.ReferencePoint)));
                client.setContactPerson(cursor.getString(cursor.getColumnIndex(Clients.ContactPerson)));
                client.setContactPersonPhone(cursor.getString(cursor.getColumnIndex(Clients.ContactPersonPhone)));
                client.setCreditLimit(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Clients.CreditLimit))));
                client.setAcumulatedCredit(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Clients.AccumulatedCredit))));
                client.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Clients.Latitude))));
                client.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Clients.Longitude))));
                return client;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return new TradingPointTemplate();
    }

    private synchronized void clearClients() {
        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.delete(Clients.Table, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception(">>> CLEARING CLIENTS <<<");
            ex.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Добавляет/редактирует товар в бд.
     */
    public synchronized void addProduct(Context context, SoapObject items, Boolean clear) {
        try {
            if (clear)
                clearProducts();
            sqLiteDatabase.beginTransaction();
            //L.info("Products and Balances");
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject brand = (SoapObject) items.getProperty(i);
                SoapObject productsByBrand = (SoapObject) brand.getProperty(1);
                for (int l = 0; productsByBrand.getPropertyCount() > l; l++) {
                    SoapObject products = (SoapObject) productsByBrand.getProperty(l);
                    SoapObject productList = (SoapObject) products.getProperty(1);
                    final String seria = (products.getProperty("ProductSeries").toString()
                            .equals("anyType{}") ? context.getString(R.string.hint_without_series) : products.getProperty("ProductSeries").toString());

                    for (int p = 0; productList.getPropertyCount() > p; p++) {
                        SoapObject product = (SoapObject) productList.getProperty(p);

                        ContentValues fields = new ContentValues();
                        fields.put(Products.GroupName, brand.getProperty("ProductBrand").toString());
                        fields.put(Products.Series, seria);
                        fields.put(Products.WarehouseCode, product.getProperty("CodeSklad").toString());
                        fields.put(Products.ProductCode, product.getProperty("CodeProduct").toString());
                        fields.put(Products.ProductName, product.getProperty("NameProduct").toString());
                        fields.put(Products.Have, Integer.parseInt(product.getProperty("Have").toString()));
                        fields.put(Products.Reserved, Integer.parseInt(product.getProperty("Reserved").toString()));
                        fields.put(Products.Available, Integer.parseInt(product.getProperty("Aviable").toString()));
                        fields.put(Products.Weight, product.getProperty("Weight").toString());
                        fields.put(Products.Capacity, product.getProperty("Capacity").toString());
                        // Артикул
                        fields.put(Products.VendorCode, product.getProperty("VendorCode").toString());

                        //L.info("-----------------------------------------");
                        //L.info("Brand:.." + brand.getProperty("ProductBrand").toString());
                        //L.info("Series:." + seria);
                        //L.info(product.getProperty("CodeProduct").toString() + " " + product.getProperty("NameProduct").toString());
                        sqLiteDatabase.insertWithOnConflict(Products.Table, null, fields,
                                SQLiteDatabase.CONFLICT_REPLACE);
                    }
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVING PRODUCT <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public synchronized void updateBalance(String warehouseCode, String goodsCode, String available) {
        ContentValues fields = new ContentValues();
        fields.put(Products.Available, available);

        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.updateWithOnConflict(Products.Table, fields,
                    String.format("%s = '%s' and %s = '%s'", Products.WarehouseCode, warehouseCode,
                            Products.ProductCode, goodsCode), null, SQLiteDatabase.CONFLICT_REPLACE);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE UPDATING PRODUCT <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Очищает таблицу с товарами
     */
    private synchronized void clearProducts() {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(Products.Table, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE CLEARING PRODUCT TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Определяет, необходимо ли обновлять список товара
     *
     * @param days количество дней по истичению которых нужно обновить справочник
     */
    public synchronized boolean needToUpdatePriceTypes(int days) {

        // @author MrJ
        // TODO: Запросим дату последнего изменения цены и сверим

        try {
            return !L.UpdateTimePriceList.equals(L.oldUpdateTimePriceList);
        } catch (Exception ex) {
            Log.d("Exc", "Error");
        }
        //

        // TODO: Необходимо учесть, что перезаходят разные пользователи с разными настройками
        if (days == -1)
            return false;

        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select %s from %s limit 1",
                PriceType.CreatedDate, PriceType.Table), null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                long last_day = System.currentTimeMillis() - Long.parseLong(cursor.getString(0));
                return (TimeUnit.DAYS.convert(last_day, TimeUnit.MILLISECONDS) >= days);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return true;
    }

    /**
     * Очищает таблицу типов цен
     */
    private synchronized void clearPriceType() {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(PriceType.Table, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE CLEARING PRICE TYPE TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Сохраняет тип товара
     *
     * @param items list of price types
     */
    public synchronized void savePriceType(SoapObject items) {
        clearPriceType();
        sqLiteDatabase.beginTransaction();
        try {
            final String created_date = System.currentTimeMillis() + "";
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject priceType = (SoapObject) items.getProperty(i);
                //L.info(String.format("%s: %s ", priceType.getProperty("Code"), priceType.getProperty("Name")));

                ContentValues fields = new ContentValues();
                fields.put(PriceType.Code, priceType.getProperty("Code").toString());
                fields.put(PriceType.Name, priceType.getProperty("Name").toString());
                fields.put(PriceType.CreatedDate, created_date);

                sqLiteDatabase.insertWithOnConflict(PriceType.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVE PRICE TYPE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Определяет, необходимо ли обновлять список товара
     *
     * @param days количество дней по истичению которых нужно обновить справочник
     */
    public synchronized boolean needToUpdateBusRegions(int days) {
        // TODO: Необходимо учесть, что перезаходят разные пользователи с разными настройками
        if (days == -1)
            return false;

        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select %s from %s limit 1",
                BusinessRegions.UpdatedDate, BusinessRegions.Table), null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                long last_day = System.currentTimeMillis() - Long.parseLong(cursor.getString(0));
                return (TimeUnit.DAYS.convert(last_day, TimeUnit.MILLISECONDS) >= days);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return true;
    }

    /**
     * Сохраняет бизнес регион агента
     *
     * @param items list of business regions
     */
    public synchronized void saveBusinessRegions(SoapObject items) {
        clearBusinessRegions();
        sqLiteDatabase.beginTransaction();
        try {
            final String created_date = System.currentTimeMillis() + "";
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject businessRegions = (SoapObject) items.getProperty(i);
                //L.info(String.format("%s: %s ", businessRegions.getProperty("Code"), businessRegions.getProperty("Name")));

                ContentValues fields = new ContentValues();
                fields.put(BusinessRegions.Code, businessRegions.getProperty("Code").toString());
                fields.put(BusinessRegions.Name, businessRegions.getProperty("Name").toString());
                fields.put(BusinessRegions.UpdatedDate, created_date);

                sqLiteDatabase.insertWithOnConflict(BusinessRegions.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVE PRICE TYPE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Очищает таблицу бизнес регионов
     */
    private synchronized void clearBusinessRegions() {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(BusinessRegions.Table, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE CLEARING PRICE TYPE TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Проверяет, необходимо ли обновить список цен.
     * Обновление происходит раз в день
     */
    public synchronized boolean needToUpdatePriceList() {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select %s from %s limit 1",
                PriceList.UpdatedDate, PriceList.Table), null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                String updatedDate = cursor.getString(0),
                        currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                .format(new Date(System.currentTimeMillis()));

                return (!updatedDate.equals(currentDate));
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return true;
    }

    /**
     * Добавляет/редактирует цены в бд.
     *
     * @param items price list
     */
    public synchronized void addPrice(SoapObject items) {
        clearPriceList();
        final String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));

        try {
            sqLiteDatabase.beginTransaction();
            //L.info("Price list");
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject priceItem = (SoapObject) items.getProperty(i);

                ContentValues fields = new ContentValues();
                fields.put(PriceList.Code, priceItem.getProperty("CodeTypePrice").toString());
                fields.put(PriceList.ProductCode, priceItem.getProperty("CodeProduct").toString());
                fields.put(PriceList.Price, priceItem.getProperty("Price").toString());
                fields.put(PriceList.UpdatedDate, currentDate);

                //L.info("----------------------------");
                //L.info(String.format("Code:.........%s", priceItem.getProperty("CodeTypePrice").toString()));
                //L.info(String.format("Product Code:.%s", priceItem.getProperty("CodeProduct").toString()));
                //L.info(String.format("Price:........%s", priceItem.getProperty("Price").toString()));

                sqLiteDatabase.insertWithOnConflict(PriceList.Table, null, fields,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVE PRICE LIST <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Очищает таблицу с товарами
     */
    private synchronized void clearPriceList() {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(PriceList.Table, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE CLEARING PRICE LIST TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * @return business regions list
     */
    public synchronized ArrayList<BusinessRegionsTemplate> getBusinessRegions() {
        ArrayList<BusinessRegionsTemplate> result;

        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", BusinessRegions.Table), null);
        try {
            if (cursor.moveToFirst()) {
                result = new ArrayList<>(cursor.getCount());
                do {
                    BusinessRegionsTemplate item = new BusinessRegionsTemplate();
                    item.setCode(cursor.getString(cursor.getColumnIndex(BusinessRegions.Code)));
                    item.setName(cursor.getString(cursor.getColumnIndex(BusinessRegions.Name)));
                    result.add(item);
                }
                while (cursor.moveToNext());
            } else
                result = new ArrayList<>(1);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return result;
    }

    /**
     * @return pricy type list
     */
    public synchronized ArrayList<PriceTypeTemplate> getPriceTypeList() {
        ArrayList<PriceTypeTemplate> result = null;

        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", PriceType.Table), null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = new ArrayList<>(cursor.getCount());
                do {
                    PriceTypeTemplate item = new PriceTypeTemplate();
                    item.setCode(cursor.getString(cursor.getColumnIndex(PriceType.Code)));
                    item.setName(cursor.getString(cursor.getColumnIndex(PriceType.Name)));
                    result.add(item);
                }
                while (cursor.moveToNext());
            } else
                result = new ArrayList<>(1);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return result;
    }

    /**
     * @return list of goods, sorted by price type
     */
    public synchronized ArrayList<ProductTemplate> getProductsList() {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s order by %s and %s",
                Products.Table, Products.GroupName, Products.Series), null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                ArrayList<ProductTemplate> result = new ArrayList<>(cursor.getCount());

                ProductTemplate brand = new ProductTemplate();
                brand.setBrand(cursor.getString(cursor.getColumnIndex(Products.GroupName)));

                ArrayList<SeriesTemplate> seriesList = new ArrayList<>();
                //ArrayList<GoodsByBrandTemplate> goodsListByBrand = new ArrayList<>();
                ArrayList<GoodsByBrandTemplate> goodsListBySeries = new ArrayList<>();

                String brandTemp = brand.getBrand();
                String seriesTemp = cursor.getString(cursor.getColumnIndex(Products.Series));

                do {
                    GoodsByBrandTemplate subItem = new GoodsByBrandTemplate();
                    subItem.setWarehouseCode(cursor.getString(cursor.getColumnIndex(Products.WarehouseCode)));
                    subItem.setProductCode(cursor.getString(cursor.getColumnIndex(Products.ProductCode)));
                    subItem.setProductName(cursor.getString(cursor.getColumnIndex(Products.ProductName)));
                    //subItem.setHave(cursor.getInt(cursor.getColumnIndex(Products.Have)));
                    //subItem.setReserved(cursor.getInt(cursor.getColumnIndex(Products.Reserved)));
                    subItem.setAvailable(cursor.getInt(cursor.getColumnIndex(Products.Available)));
                    subItem.setWeight(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Products.Weight))));
                    subItem.setCapacity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Products.Capacity))));
                    subItem.setMinAmount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Products.MinAmount))));

                    if (!brandTemp.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(Products.GroupName)))) {
                        brandTemp = cursor.getString(cursor.getColumnIndex(Products.GroupName));

                        if (!seriesTemp.equals("")) {
                            SeriesTemplate seriesTemplate = new SeriesTemplate();
                            seriesTemplate.setName(seriesTemp);
                            seriesTemplate.setGoods(goodsListBySeries);
                            seriesList.add(seriesTemplate);

                            seriesTemp = cursor.getString(cursor.getColumnIndex(Products.Series));
                        }

                        brand.setSeries(seriesList);
                        //brand.setGoods(goodsListByBrand);
                        result.add(brand);

                        brand = new ProductTemplate();
                        brand.setBrand(brandTemp);

                        seriesList = new ArrayList<>();
                        //goodsListByBrand = new ArrayList<>();
                        goodsListBySeries = new ArrayList<>();
                    }

                    if (!seriesTemp.equals("")) {
                        if (!seriesTemp.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(Products.Series)))) {
                            SeriesTemplate seriesTemplate = new SeriesTemplate();
                            seriesTemplate.setName(seriesTemp);
                            seriesTemplate.setGoods(goodsListBySeries);
                            seriesList.add(seriesTemplate);

                            seriesTemp = cursor.getString(cursor.getColumnIndex(Products.Series));
                            goodsListBySeries = new ArrayList<>();
                        }
                        {
                            subItem.setBrand(brandTemp);
                            subItem.setSeries(seriesTemp);
                            goodsListBySeries.add(subItem);
                        }
                    } else {
                        subItem.setBrand(brandTemp);
                        //goodsListByBrand.add(subItem);
                    }

                }
                while (cursor.moveToNext());

                if (!seriesTemp.equals("")) {
                    SeriesTemplate seriesTemplate = new SeriesTemplate();
                    seriesTemplate.setName(seriesTemp);
                    seriesTemplate.setGoods(goodsListBySeries);
                    seriesList.add(seriesTemplate);
                }

                brand.setSeries(seriesList);
                //brand.setGoods(goodsListByBrand);
                result.add(brand);

                return result;
            }
            return new ArrayList<>(1);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * @param priceType тип цены
     * @return товары
     */
    public synchronized ArrayList<ProductTemplate> getProducts(String priceType, String warehouseCode) {
        HashMap<String, PriceTemplate> priceList = getPriceListByPriceType(priceType);

        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s order by %s and %s",
                Products.Table, Products.GroupName, Products.Series), null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                ArrayList<ProductTemplate> productsList = new ArrayList<>();
                //* бренд
                ProductTemplate brand = new ProductTemplate();
                brand.setBrand(cursor.getString(cursor.getColumnIndex(Products.GroupName)));
                //* список серий и товаров по сериям
                ArrayList<SeriesTemplate> seriesList = new ArrayList<>();
                //* товары по сериям
                ArrayList<GoodsByBrandTemplate> goodsListBySeries = new ArrayList<>();

                String brandTemp = brand.getBrand();
                String seriesTemp = cursor.getString(cursor.getColumnIndex(Products.Series));

                boolean stock4AllSeries = false;

                do {
                    if (!warehouseCode.equals(cursor.getString(cursor.getColumnIndex(Products.WarehouseCode)))) {
                        continue;
                    }


                    GoodsByBrandTemplate subItem = new GoodsByBrandTemplate();
                    subItem.setWarehouseCode(cursor.getString(cursor.getColumnIndex(Products.WarehouseCode)));
                    subItem.setProductCode(cursor.getString(cursor.getColumnIndex(Products.ProductCode)));
                    subItem.setProductName(cursor.getString(cursor.getColumnIndex(Products.ProductName)));
                    //subItem.setHave(cursor.getInt(cursor.getColumnIndex(Products.Have)));
                    //subItem.setReserved(cursor.getInt(cursor.getColumnIndex(Products.Reserved)));
                    subItem.setAvailable(cursor.getInt(cursor.getColumnIndex(Products.Available)));
                    subItem.setWeight(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Products.Weight))));
                    subItem.setCapacity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Products.Capacity))));
                    subItem.setMinAmount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Products.MinAmount))));
                    // Артикул
                    subItem.setVendorCode(cursor.getString(cursor.getColumnIndex(Products.VendorCode)));

                    if (priceList.containsKey(subItem.getProductCode())) {
                        PriceTemplate price = priceList.get(subItem.getProductCode());
                        subItem.setOriginalPrice(price.getPrice());
                        subItem.setDiscountValue(price.getDiscount());
                        subItem.setPrice((price.getDiscount() == 0 ? price.getPrice() : price.getNewPrice()));
                    }

                    if (!seriesTemp.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(Products.Series)))) {
                        SeriesTemplate seriesTemplate = new SeriesTemplate();
                        if (stock4AllSeries)
                            seriesTemplate.setMinAmount(goodsListBySeries.get(0).getMinAmount());
                        seriesTemplate.setName(seriesTemp);
                        seriesTemplate.setGoods(goodsListBySeries);
                        seriesList.add(seriesTemplate);

                        seriesTemp = cursor.getString(cursor.getColumnIndex(Products.Series));
                        goodsListBySeries = new ArrayList<>();
                    }

                    if (!brandTemp.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(Products.GroupName)))) {
                        brandTemp = cursor.getString(cursor.getColumnIndex(Products.GroupName));
                        brand.setSeries(seriesList);
                        productsList.add(brand);

                        brand = new ProductTemplate();
                        brand.setBrand(brandTemp);
                        seriesList = new ArrayList<>();
                        goodsListBySeries = new ArrayList<>();
                    }

                    subItem.setBrand(brandTemp);
                    subItem.setSeries(seriesTemp);
                    goodsListBySeries.add(subItem);

                    stock4AllSeries = (subItem.getMinAmount() != 0);
                }
                while (cursor.moveToNext());

                SeriesTemplate seriesTemplate = new SeriesTemplate();
                if (stock4AllSeries)
                    seriesTemplate.setMinAmount(goodsListBySeries.get(0).getMinAmount());
                seriesTemplate.setName(seriesTemp);
                seriesTemplate.setGoods(goodsListBySeries);
                seriesList.add(seriesTemplate);

                brand.setSeries(seriesList);
                productsList.add(brand);

                /*for (ProductTemplate product: productsList)
                {
                    L.info("---------------------------------");
                    L.info("Brand.....: " + product.getBrand());
                    for (SeriesTemplate series: product.getSeries())
                    {
                        L.info("...Series.....: " + series.getName());
                        L.info("...Min.Amount.: " + series.getMinAmount());
                        for (GoodsByBrandTemplate goods: series.getGoods())
                        {
                            L.info("      ---------------------------------");
                            L.info(String.format("......Code...........: %s", goods.getProductCode()));
                            L.info(String.format("......Name...........: %s", goods.getProductName()));
                            L.info(String.format("......Current Price..: %s", goods.getPrice()));
                            L.info(String.format("......Origin Price...: %s", goods.getOriginalPrice()));
                            L.info(String.format("......Discount Value.: %s", goods.getDiscountValue()));
                            L.info(String.format("......Min. Amount....: %s", goods.getMinAmount()));
                        }
                    }
                }*/

                return productsList;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return new ArrayList<>(1);
    }

    /**
     * @return Список товаров, которые выдаются в подарок
     */
    public synchronized ArrayList<GoodsByBrandTemplate> getGiftList(String ptCode) {
        HashMap<String, PriceTemplate> priceList = getPriceListByPriceType(ptCode);

        //String.format("select a.%s, b.* from %s, %s where a.%s = b.%s",
        //        Products.Series, Products.Table, GiftList.Table, Products.ProductCode, GiftList.ProductCode);

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + GiftList.Table, null);
        if (cursor != null && cursor.moveToFirst()) {
            ArrayList<GoodsByBrandTemplate> giftList = new ArrayList<>(cursor.getCount());
            do {
                GoodsByBrandTemplate goods = new GoodsByBrandTemplate();
                goods.setProductCode(cursor.getString(cursor.getColumnIndex(GiftList.ProductCode)));
                goods.setProductName(cursor.getString(cursor.getColumnIndex(GiftList.ProductName)));

                Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = '%s'",
                        Products.Table, Products.ProductCode, goods.getProductCode()), null);
                if (cursor1 != null && cursor1.moveToFirst()) {
                    goods.setWeight(Double.parseDouble(cursor1.getString(cursor1.getColumnIndex(Products.Weight))));
                    goods.setCapacity(Double.parseDouble(cursor1.getString(cursor1.getColumnIndex(Products.Capacity))));
                    cursor1.close();
                }

                goods.setOriginalPrice(0);
                goods.setPrice((priceList.containsKey(goods.getProductCode()) ? priceList.get(goods.getProductCode()).getPrice() : 0));
                goods.setDiscountValue(100);
                goods.setCount(cursor.getInt(cursor.getColumnIndex(GiftList.Amount)));
                goods.setMinAmount(cursor.getInt(cursor.getColumnIndex(GiftList.MinAmount)));
                giftList.add(goods);
            }
            while (cursor.moveToNext());
            cursor.close();
            return giftList;
        }
        return new ArrayList<>(1);
    }

    public synchronized HashMap<String, ArrayList<GoodsByBrandTemplate>> getGiftListNew(String ptCode) {
        HashMap<String, PriceTemplate> priceList = getPriceListByPriceType(ptCode);

        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select a.%s, b.* from %s a, %s b where a.%s = b.%s order by a.%s",
                Products.Series, Products.Table, GiftList.Table, Products.ProductCode, GiftList.ProductCode, Products.Series), null);

        if (cursor != null && cursor.moveToFirst()) {
            HashMap<String, ArrayList<GoodsByBrandTemplate>> giftList = new HashMap<>(cursor.getCount());

            String temp = cursor.getString(0);
            ArrayList<GoodsByBrandTemplate> goodsList = new ArrayList<>();

            do {
                L.info(temp);

                GoodsByBrandTemplate goods = new GoodsByBrandTemplate();
                goods.setSeries(cursor.getString(0));
                goods.setProductCode(cursor.getString(cursor.getColumnIndex(GiftList.ProductCode)));
                goods.setProductName(cursor.getString(cursor.getColumnIndex(GiftList.ProductName)));

                Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = '%s'",
                        Products.Table, Products.ProductCode, goods.getProductCode()), null);
                if (cursor1 != null && cursor1.moveToFirst()) {
                    goods.setWeight(Double.parseDouble(cursor1.getString(cursor1.getColumnIndex(Products.Weight))));
                    goods.setCapacity(Double.parseDouble(cursor1.getString(cursor1.getColumnIndex(Products.Capacity))));
                    cursor1.close();
                }

                goods.setOriginalPrice(0);
                goods.setPrice((priceList.containsKey(goods.getProductCode()) ? priceList.get(goods.getProductCode()).getPrice() : 0));
                goods.setDiscountValue(100);
                goods.setCount(cursor.getInt(cursor.getColumnIndex(GiftList.Amount)));
                goods.setMinAmount(cursor.getInt(cursor.getColumnIndex(GiftList.MinAmount)));

                if (!temp.equalsIgnoreCase(cursor.getString(0))) {
                    giftList.put(temp, goodsList);
                    temp = cursor.getString(0);
                    goodsList = new ArrayList<>();
                }

                goodsList.add(goods);
            }
            while (cursor.moveToNext());
            cursor.close();

            giftList.put(temp, goodsList);

            return giftList;
        }
        return new HashMap<>(1);
    }

    /**
     * @param priceType код типа цены
     * @return list of map product code and price
     */
    public synchronized HashMap<String, PriceTemplate> getPriceListByPriceType(String priceType) {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = '%s'",
                PriceList.Table, PriceList.Code, priceType), null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                HashMap<String, PriceTemplate> result = new HashMap<>(cursor.getCount());
                do {
                    PriceTemplate price = new PriceTemplate();
                    price.setPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndex(PriceList.Price))));
                    price.setNewPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndex(PriceList.NewPrice))));
                    price.setDiscount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(PriceList.Discount))));
                    result.put(cursor.getString(cursor.getColumnIndex(PriceList.ProductCode)), price);
                }
                while (cursor.moveToNext());
                return result;
            }
            return new HashMap<>(1);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * Временное сохранение заказа в бд приложения
     *
     * @param order сформированный заказ
     */
    public synchronized long saveOrder(MadeOrderTemplate order) {
        long rowId = -1;
        ContentValues fields = new ContentValues();
        fields.put(Orders.OnCredit, Util.BoolToInt(order.isOnCredit()));
        fields.put(Orders.UserCode, order.getUserCode());
        fields.put(Orders.OrderType, order.getOrderType());
        fields.put(Orders.TradingPointCode, order.getTpCode());
        fields.put(Orders.TradingPoint, order.getTpName());
        fields.put(Orders.PriceType, order.getPriceType());
        fields.put(Orders.PriceTypeName, order.getPriceTypeName());
        fields.put(Orders.Title, order.getOrderTitle());
        fields.put(Orders.PhotoLink1, order.getPhoto1());
        fields.put(Orders.PhotoLink2, order.getPhoto2());
        fields.put(Orders.PhotoLink3, order.getPhoto3());
        fields.put(Orders.Latitude, order.getLatitude() + "");
        fields.put(Orders.Longitude, order.getLongitude() + "");
        fields.put(Orders.UploadDateView, order.getUploadDateView());
        fields.put(Orders.UploadDateValue, order.getUploadDateValue());
        fields.put(Orders.CommentTo, order.getCommentTo());
        fields.put(Orders.Comment, order.getComment());
        fields.put(Orders.Weight, order.getWeight() + "");
        fields.put(Orders.Capacity, order.getCapacity() + "");
        fields.put(Orders.TotalPrice, order.getTotalPrice() + "");
        fields.put(Orders.Status, order.getStatus().getValue());
        fields.put(Orders.CreatedDate, System.currentTimeMillis() + "");
        fields.put(Orders.Description, order.getDescription());
        fields.put(Orders.CodeWarehouse, order.getStockTemplate().getCode());
        fields.put(Orders.CodeOrg, order.getOrganizationTemplate().getCode());
        fields.put(Orders.CodeOrg, order.getCodeContract());
        fields.put(Orders.CodeContract, order.getCodeContract());

        sqLiteDatabase.beginTransaction();
        try {
            rowId = sqLiteDatabase.insertWithOnConflict(Orders.Table, null, fields,
                    SQLiteDatabase.CONFLICT_REPLACE);

            for (GoodsByBrandTemplate goodsItem : order.getGoodsList().values()) {
                ContentValues goodsFields = new ContentValues();
                goodsFields.put(OrdersGoods.OrderId, rowId);
                goodsFields.put(OrdersGoods.Brand, goodsItem.getBrand());
                goodsFields.put(OrdersGoods.Series, goodsItem.getSeries());
                goodsFields.put(OrdersGoods.WarehouseCode, goodsItem.getWarehouseCode());
                goodsFields.put(OrdersGoods.ProductCode, goodsItem.getProductCode());
                goodsFields.put(OrdersGoods.ProductName, goodsItem.getProductName());
                goodsFields.put(OrdersGoods.Amount, goodsItem.getAmount());
                goodsFields.put(OrdersGoods.Available, goodsItem.getAvailable());
                goodsFields.put(OrdersGoods.Price, goodsItem.getPrice() + "");
                goodsFields.put(OrdersGoods.Total, goodsItem.getTotal() + "");
                goodsFields.put(OrdersGoods.Weight, goodsItem.getWeight() + "");
                goodsFields.put(OrdersGoods.Capacity, goodsItem.getCapacity() + "");
                goodsFields.put(OrdersGoods.MinAmount, goodsItem.getMinAmount());
                goodsFields.put(OrdersGoods.OriginalPrice, goodsItem.getOriginalPrice());
                goodsFields.put(OrdersGoods.DiscountValue, goodsItem.getDiscountValue());
                // Количество подарков по акции
                goodsFields.put(OrdersGoods.GiftAmount, goodsItem.getGiftAmount());
                sqLiteDatabase.insertWithOnConflict(OrdersGoods.Table, null, goodsFields,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }

            for (CompetitorScoutingTemplate competitor : order.getCompetitorList()) {
                ContentValues compFields = new ContentValues();
                compFields.put(OrdersCompetitorScouting.OrderId, rowId);
                compFields.put(OrdersCompetitorScouting.Name, competitor.getName());
                compFields.put(OrdersCompetitorScouting.Goods, competitor.getGoods());
                compFields.put(OrdersCompetitorScouting.Price, competitor.getPrice());
                sqLiteDatabase.insertWithOnConflict(OrdersCompetitorScouting.Table, null, compFields,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }

            Map<String, String> notEnoughGoods = order.getNotEnoughGoods();
            for (String key : notEnoughGoods.keySet()) {
                ContentValues notEGoodsFields = new ContentValues();
                notEGoodsFields.put(OrdersNotEnoughGoods.OrderId, rowId);
                notEGoodsFields.put(OrdersNotEnoughGoods.ProductCode, key);
                notEGoodsFields.put(OrdersNotEnoughGoods.Available, notEnoughGoods.get(key));
                sqLiteDatabase.insertWithOnConflict(OrdersNotEnoughGoods.Table, null, notEGoodsFields,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }

            for (String brand : order.getNotEnoughByBrand()) {
                ContentValues notEByBndFields = new ContentValues();
                notEByBndFields.put(OrdersNotEnoughGoodsByBrand.OrderId, rowId);
                notEByBndFields.put(OrdersNotEnoughGoodsByBrand.Brand, brand);
                sqLiteDatabase.insertWithOnConflict(OrdersNotEnoughGoodsByBrand.Table, null, notEByBndFields,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }

            for (String series : order.getNotEnoughBySeries()) {
                ContentValues notEBySrsFields = new ContentValues();
                notEBySrsFields.put(OrdersNotEnoughGoodsBySeries.OrderId, rowId);
                notEBySrsFields.put(OrdersNotEnoughGoodsBySeries.Series, series);
                sqLiteDatabase.insertWithOnConflict(OrdersNotEnoughGoodsBySeries.Table, null, notEBySrsFields,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }

            for (GoodsByBrandTemplate gift : order.getGiftList()) {
                ContentValues giftFields = new ContentValues();
                giftFields.put(OrdersGiftList.OrderId, rowId);
                giftFields.put(OrdersGiftList.ForProduct, gift.getForProduct());
                giftFields.put(OrdersGiftList.ProductCode, gift.getProductCode());
                giftFields.put(OrdersGiftList.ProductName, gift.getProductName());
                giftFields.put(OrdersGiftList.Price, gift.getPrice());
                giftFields.put(OrdersGiftList.Discount, gift.getDiscountValue());
                giftFields.put(OrdersGiftList.Count, gift.getCount());
                giftFields.put(OrdersGiftList.Amount, gift.getAmount());
                giftFields.put(OrdersGiftList.MinAmount, gift.getMinAmount());
                giftFields.put(OrdersGiftList.Weight, gift.getWeight());
                giftFields.put(OrdersGiftList.Capacity, gift.getCapacity());
                giftFields.put(OrdersGiftList.AmountBySeries, gift.getAmountBySeries());
                sqLiteDatabase.insertWithOnConflict(OrdersGiftList.Table, null, giftFields, SQLiteDatabase.CONFLICT_REPLACE);
            }

            for (CreditVisitTemplate creditVisit : order.getCreditVisits()) {
                ContentValues creditVisitFields = new ContentValues();
                creditVisitFields.put(CreditVisits.OrderId, rowId);
                creditVisitFields.put(CreditVisits.VisitDateView, creditVisit.getVisitDate());
                creditVisitFields.put(CreditVisits.VisitDateValue, creditVisit.getVisitDateValue());
                creditVisitFields.put(CreditVisits.Sum, creditVisit.getTakeSum());
                sqLiteDatabase.insertWithOnConflict(CreditVisits.Table, null, creditVisitFields, SQLiteDatabase.CONFLICT_REPLACE);
            }

            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVING ORDER <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
            fields.clear();
        }
        return rowId;
    }

    /**
     * Редактирование заказа
     *
     * @param order созданный заказ
     */
    public synchronized void editOrder(MadeOrderTemplate order) {
        ContentValues fields = new ContentValues();
        fields.put(Orders.OrderType, order.getOrderType());
        fields.put(Orders.OnCredit, Util.BoolToInt(order.isOnCredit()));
        fields.put(Orders.PriceType, order.getPriceType());
        fields.put(Orders.PriceTypeName, order.getPriceTypeName());
        fields.put(Orders.Title, order.getOrderTitle());
        fields.put(Orders.UploadDateView, order.getUploadDateView());
        fields.put(Orders.UploadDateValue, order.getUploadDateValue());
        fields.put(Orders.CommentTo, order.getCommentTo());
        fields.put(Orders.Comment, order.getComment());
        fields.put(Orders.Weight, order.getWeight() + "");
        fields.put(Orders.Capacity, order.getCapacity() + "");
        fields.put(Orders.TotalPrice, order.getTotalPrice() + "");
        fields.put(Orders.PhotoLink1, order.getPhoto1());
        fields.put(Orders.PhotoLink2, order.getPhoto2());
        fields.put(Orders.PhotoLink3, order.getPhoto3());
        fields.put(Orders.Status, order.getStatus().getValue());
        fields.put(Orders.Description, order.getDescription());
        fields.put(Orders.CodeWarehouse, order.getStockTemplate().getCode());
        fields.put(Orders.CodeOrg, order.getOrganizationTemplate().getCode());
        fields.put(Orders.CodeContract, order.getCodeContract());

        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.updateWithOnConflict(Orders.Table, fields, String.format("%s = %s",
                    Orders.RowId, order.getRowId()), null, SQLiteDatabase.CONFLICT_REPLACE);

            sqLiteDatabase.delete(OrdersGoods.Table, String.format("%s = %s", OrdersGoods.OrderId, order.getRowId()), null);
            for (GoodsByBrandTemplate goodsItem : order.getGoodsList().values()) {
                ContentValues goodsFields = new ContentValues();
                goodsFields.put(OrdersGoods.OrderId, order.getRowId());
                goodsFields.put(OrdersGoods.Brand, goodsItem.getBrand());
                goodsFields.put(OrdersGoods.Series, goodsItem.getSeries());
                goodsFields.put(OrdersGoods.WarehouseCode, goodsItem.getWarehouseCode());
                goodsFields.put(OrdersGoods.ProductCode, goodsItem.getProductCode());
                goodsFields.put(OrdersGoods.ProductName, goodsItem.getProductName());
                goodsFields.put(OrdersGoods.Amount, goodsItem.getAmount());
                goodsFields.put(OrdersGoods.Available, goodsItem.getAvailable());
                goodsFields.put(OrdersGoods.Price, goodsItem.getPrice() + "");
                goodsFields.put(OrdersGoods.Total, goodsItem.getTotal() + "");
                goodsFields.put(OrdersGoods.Weight, goodsItem.getWeight() + "");
                goodsFields.put(OrdersGoods.Capacity, goodsItem.getCapacity() + "");
                goodsFields.put(OrdersGoods.MinAmount, goodsItem.getMinAmount());
                goodsFields.put(OrdersGoods.OriginalPrice, goodsItem.getOriginalPrice());
                goodsFields.put(OrdersGoods.DiscountValue, goodsItem.getDiscountValue());
                // Количество подарков по акции
                goodsFields.put(OrdersGoods.GiftAmount, goodsItem.getGiftAmount());
                sqLiteDatabase.insertWithOnConflict(OrdersGoods.Table, null, goodsFields, SQLiteDatabase.CONFLICT_REPLACE);
            }

            sqLiteDatabase.delete(OrdersCompetitorScouting.Table, String.format("%s = %s",
                    OrdersCompetitorScouting.OrderId, order.getRowId()), null);
            for (CompetitorScoutingTemplate competitorItem : order.getCompetitorList()) {
                ContentValues compFields = new ContentValues();
                compFields.put(OrdersCompetitorScouting.OrderId, order.getRowId());
                compFields.put(OrdersCompetitorScouting.Name, competitorItem.getName());
                compFields.put(OrdersCompetitorScouting.Goods, competitorItem.getGoods());
                compFields.put(OrdersCompetitorScouting.Price, competitorItem.getPrice());
                sqLiteDatabase.insertWithOnConflict(OrdersCompetitorScouting.Table, null, compFields,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }

            sqLiteDatabase.delete(OrdersNotEnoughGoods.Table, String.format("%s = %s",
                    OrdersNotEnoughGoods.OrderId, order.getRowId()), null);
            Map<String, String> notEnoughGoods = order.getNotEnoughGoods();
            for (String key : notEnoughGoods.keySet()) {
                ContentValues notEGoodsFields = new ContentValues();
                notEGoodsFields.put(OrdersNotEnoughGoods.OrderId, order.getRowId());
                notEGoodsFields.put(OrdersNotEnoughGoods.ProductCode, key);
                notEGoodsFields.put(OrdersNotEnoughGoods.Available, notEnoughGoods.get(key));
                sqLiteDatabase.insertWithOnConflict(OrdersNotEnoughGoods.Table, null, notEGoodsFields,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }

            sqLiteDatabase.delete(OrdersNotEnoughGoodsByBrand.Table, String.format("%s = %s",
                    OrdersNotEnoughGoodsByBrand.OrderId, order.getRowId()), null);
            for (String brand : order.getNotEnoughByBrand()) {
                ContentValues notEByBndFields = new ContentValues();
                notEByBndFields.put(OrdersNotEnoughGoodsByBrand.OrderId, order.getRowId());
                notEByBndFields.put(OrdersNotEnoughGoodsByBrand.Brand, brand);
                sqLiteDatabase.insertWithOnConflict(OrdersNotEnoughGoodsByBrand.Table, null, notEByBndFields,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }

            sqLiteDatabase.delete(OrdersNotEnoughGoodsBySeries.Table, String.format("%s = %s",
                    OrdersNotEnoughGoodsBySeries.OrderId, order.getRowId()), null);
            for (String series : order.getNotEnoughBySeries()) {
                ContentValues notEBySrsFields = new ContentValues();
                notEBySrsFields.put(OrdersNotEnoughGoodsBySeries.OrderId, order.getRowId());
                notEBySrsFields.put(OrdersNotEnoughGoodsBySeries.Series, series);
                sqLiteDatabase.insertWithOnConflict(OrdersNotEnoughGoodsBySeries.Table, null, notEBySrsFields,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }

            sqLiteDatabase.delete(OrdersGiftList.Table, String.format("%s = %s", OrdersGiftList.OrderId, order.getRowId()), null);
            for (GoodsByBrandTemplate gift : order.getGiftList()) {
                ContentValues giftFields = new ContentValues();
                giftFields.put(OrdersGiftList.OrderId, order.getRowId());
                giftFields.put(OrdersGiftList.ForProduct, gift.getForProduct());
                giftFields.put(OrdersGiftList.ProductCode, gift.getProductCode());
                giftFields.put(OrdersGiftList.ProductName, gift.getProductName());
                giftFields.put(OrdersGiftList.Price, gift.getPrice());
                giftFields.put(OrdersGiftList.Discount, gift.getDiscountValue());
                giftFields.put(OrdersGiftList.Count, gift.getCount());
                giftFields.put(OrdersGiftList.Amount, gift.getAmount());
                giftFields.put(OrdersGiftList.MinAmount, gift.getMinAmount());
                giftFields.put(OrdersGiftList.Weight, gift.getWeight());
                giftFields.put(OrdersGiftList.Capacity, gift.getCapacity());
                giftFields.put(OrdersGiftList.AmountBySeries, gift.getAmountBySeries());
                sqLiteDatabase.insertWithOnConflict(OrdersGiftList.Table, null, giftFields, SQLiteDatabase.CONFLICT_REPLACE);
            }

            sqLiteDatabase.delete(CreditVisits.Table, String.format("%s = %s", CreditVisits.OrderId, order.getRowId()), null);
            for (CreditVisitTemplate creditVisit : order.getCreditVisits()) {
                ContentValues creditVisitFields = new ContentValues();
                creditVisitFields.put(CreditVisits.OrderId, order.getRowId());
                creditVisitFields.put(CreditVisits.VisitDateView, creditVisit.getVisitDate());
                creditVisitFields.put(CreditVisits.VisitDateValue, creditVisit.getVisitDateValue());
                creditVisitFields.put(CreditVisits.Sum, creditVisit.getTakeSum());
                sqLiteDatabase.insertWithOnConflict(CreditVisits.Table, null, creditVisitFields, SQLiteDatabase.CONFLICT_REPLACE);
            }

            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVING ORDER <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
            fields.clear();
        }
    }

    /**
     * МОЖНО УДАЛИТЬ!
     * Редактирование заказа
     *
     * @param orderId созданный заказ
     * @param desc    Описание ошибки
     */
    public synchronized void setOrdersDesc(int orderId, String desc) {
        ContentValues fields = new ContentValues();
        fields.put(Orders.Description, desc);

        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.updateWithOnConflict(Orders.Table, fields, String.format("%s = %s",
                    Orders.RowId, orderId), null, SQLiteDatabase.CONFLICT_REPLACE);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVING ORDER <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
            fields.clear();
        }
    }

    /**
     * МОЖНО УДАЛИТЬ!
     *
     * @param tp наименование торговой точки.
     * @return количество сохранённых заказов в бд приложения по торговой точке.
     */
    public synchronized int getOrderCount(String tp) {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select count(*) from %s where %s = '%s'",
                Orders.Table, Orders.TradingPointCode, tp), null);
        try {
            if (cursor != null && cursor.moveToFirst())
                return cursor.getInt(0);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return 0;
    }

    /**
     * @return краткую информацию по сохранённым заказам на телефоне
     */
    public synchronized LinkedList<OrderTemplate> getSavedOrders() {
        LinkedList<OrderTemplate> result = new LinkedList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select %s, * from %s",
                Orders.RowId, Orders.Table), null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    OrderTemplate orderTemplate = new OrderTemplate();
                    orderTemplate.setId(cursor.getInt(0));
                    orderTemplate.setTpCode(cursor.getString(cursor.getColumnIndex(Orders.TradingPointCode)));
                    // <-- @author MrJ
                    orderTemplate.setComment(cursor.getString(cursor.getColumnIndex(Orders.Comment)));
                    // -->
                    orderTemplate.setTpName(cursor.getString(cursor.getColumnIndex(Orders.TradingPoint)));
                    orderTemplate.setPriceTypeName(cursor.getString(cursor.getColumnIndex(Orders.PriceTypeName)));
                    orderTemplate.setTitle(cursor.getString(cursor.getColumnIndex(Orders.Title)));
                    orderTemplate.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.Latitude))));
                    orderTemplate.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.Latitude))));
                    orderTemplate.setOrderStatus(OrderStatus.getOrderStatusByValue(cursor.getInt(cursor.getColumnIndex(Orders.Status))));
                    //orderTemplate.setCreatedDate(Long.parseLong(cursor.getString(cursor.getColumnIndex(Orders.CreatedDate))));
                    orderTemplate.setTotalPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.TotalPrice))));
                    orderTemplate.setCreatedDate(new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault()).format(
                            new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Orders.CreatedDate))))));
                    orderTemplate.setAttention(cursor.getString(cursor.getColumnIndex(Orders.Description)));
                    result.add(orderTemplate);
                }
                while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    /**
     * @param orderId ид заказа
     * @return сформированный заказ
     */
    public synchronized MadeOrderTemplate getMadeOrderForEdit(int orderId) {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                Orders.Table, Orders.RowId, orderId), null);

        ArrayList<WarehouseTemplate> stockTemplates = getWarehouse();

        MadeOrderTemplate forEditOrderTemplate = new MadeOrderTemplate();

        if (cursor != null && cursor.moveToFirst()) {
            forEditOrderTemplate.setRowId(orderId);
            forEditOrderTemplate.setOrderType(cursor.getInt(cursor.getColumnIndex(Orders.OrderType)));
            forEditOrderTemplate.setOnCredit(Util.IntToBool(cursor.getInt(cursor.getColumnIndex(Orders.OnCredit))));
            forEditOrderTemplate.setUserCode(cursor.getString(cursor.getColumnIndex(Orders.UserCode)));
            forEditOrderTemplate.setTpCode(cursor.getString(cursor.getColumnIndex(Orders.TradingPointCode)));
            forEditOrderTemplate.setTpName(cursor.getString(cursor.getColumnIndex(Orders.TradingPoint)));
            forEditOrderTemplate.setPriceType(cursor.getString(cursor.getColumnIndex(Orders.PriceType)));
            forEditOrderTemplate.setPriceTypeName(cursor.getString(cursor.getColumnIndex(Orders.PriceTypeName)));
            forEditOrderTemplate.setOrderTitle(cursor.getString(cursor.getColumnIndex(Orders.Title)));
            forEditOrderTemplate.setUploadDateView(cursor.getString(cursor.getColumnIndex(Orders.UploadDateView)));
            forEditOrderTemplate.setUploadDateValue(cursor.getString(cursor.getColumnIndex(Orders.UploadDateValue)));
            forEditOrderTemplate.setPhoto1(cursor.getString(cursor.getColumnIndex(Orders.PhotoLink1)));
            forEditOrderTemplate.setPhoto2(cursor.getString(cursor.getColumnIndex(Orders.PhotoLink2)));
            forEditOrderTemplate.setPhoto3(cursor.getString(cursor.getColumnIndex(Orders.PhotoLink3)));
            forEditOrderTemplate.setCommentTo(cursor.getString(cursor.getColumnIndex(Orders.CommentTo)));
            forEditOrderTemplate.setComment(cursor.getString(cursor.getColumnIndex(Orders.Comment)));
            forEditOrderTemplate.setTotalPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.TotalPrice))));
            forEditOrderTemplate.setWeight(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.Weight))));
            forEditOrderTemplate.setCapacity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.Capacity))));
            forEditOrderTemplate.setStatus(OrderStatus.getOrderStatusByValue(cursor.getInt(cursor.getColumnIndex(Orders.Status))));
            forEditOrderTemplate.setDescription(cursor.getString(cursor.getColumnIndex(Orders.Description)));
            forEditOrderTemplate.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.Latitude))));
            forEditOrderTemplate.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.Longitude))));

            for (WarehouseTemplate stockTemplate : stockTemplates) {
                if (stockTemplate.getCode().equals(cursor.getString(cursor.getColumnIndex(Orders.CodeWarehouse)))) {
                    forEditOrderTemplate.setStockTemplate(stockTemplate);
                    forEditOrderTemplate.setOrganizationTemplate(stockTemplate.getOrg());
                    break;
                }
            }
            cursor.close();
        }

        Cursor cGoods = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                OrdersGoods.Table, OrdersGoods.OrderId, orderId), null);

        if (cGoods != null && cGoods.moveToFirst()) {
            Map<String, GoodsByBrandTemplate> goodsList = new HashMap<>();
            do {
                GoodsByBrandTemplate goodsItem = new GoodsByBrandTemplate();
                goodsItem.setBrand(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Brand)));
                goodsItem.setSeries(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Series)));
                goodsItem.setWarehouseCode(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.WarehouseCode)));
                goodsItem.setProductCode(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.ProductCode)));
                goodsItem.setProductName(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.ProductName)));
                goodsItem.setAmount(cGoods.getInt(cGoods.getColumnIndex(OrdersGoods.Amount)));
                goodsItem.setAvailable(cGoods.getInt(cGoods.getColumnIndex(OrdersGoods.Available)));
                goodsItem.setPrice(Double.parseDouble(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Price))));
                goodsItem.setTotal(Double.parseDouble(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Total))));
                goodsItem.setWeight(Double.parseDouble(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Weight))));
                goodsItem.setCapacity(Double.parseDouble(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Capacity))));
                goodsItem.setMinAmount(cGoods.getInt(cGoods.getColumnIndex(OrdersGoods.MinAmount)));
                goodsItem.setOriginalPrice(Double.parseDouble(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.OriginalPrice))));
                goodsItem.setDiscountValue(cGoods.getInt(cGoods.getColumnIndex(OrdersGoods.DiscountValue)));
                // Количество подарков по акции
                goodsItem.setGiftAmount(cGoods.getInt(cGoods.getColumnIndex(OrdersGoods.GiftAmount)));
                goodsList.put(goodsItem.getProductCode(), goodsItem);
            }
            while (cGoods.moveToNext());

            forEditOrderTemplate.setGoodsList(goodsList);
            cGoods.close();
        }

        Cursor cCompetitors = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                OrdersCompetitorScouting.Table, OrdersCompetitorScouting.OrderId, orderId), null);

        if (cCompetitors != null && cCompetitors.moveToFirst()) {
            LinkedList<CompetitorScoutingTemplate> competitorItems = new LinkedList<>();
            do {
                CompetitorScoutingTemplate competitorItem = new CompetitorScoutingTemplate();
                competitorItem.setName(cCompetitors.getString(cCompetitors.getColumnIndex(OrdersCompetitorScouting.Name)));
                competitorItem.setGoods(cCompetitors.getString(cCompetitors.getColumnIndex(OrdersCompetitorScouting.Goods)));
                competitorItem.setPrice(cCompetitors.getString(cCompetitors.getColumnIndex(OrdersCompetitorScouting.Price)));
                competitorItems.add(competitorItem);
            }
            while (cCompetitors.moveToNext());

            forEditOrderTemplate.setCompetitorList(competitorItems);
            cCompetitors.close();
        }

        Cursor cNotEngGoods = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                OrdersNotEnoughGoods.Table, OrdersNotEnoughGoods.OrderId, orderId), null);
        if (cNotEngGoods != null && cNotEngGoods.moveToFirst()) {
            Map<String, String> notEnoughGoods = new HashMap<>(cNotEngGoods.getColumnCount());
            do {
                notEnoughGoods.put(
                        cNotEngGoods.getString(cNotEngGoods.getColumnIndex(OrdersNotEnoughGoods.ProductCode)),
                        cNotEngGoods.getString(cNotEngGoods.getColumnIndex(OrdersNotEnoughGoods.Available)));
            }
            while (cNotEngGoods.moveToNext());

            forEditOrderTemplate.setNotEnoughGoods(notEnoughGoods);
            cNotEngGoods.close();
        }

        Cursor cNotEngByBrand = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                OrdersNotEnoughGoodsByBrand.Table, OrdersNotEnoughGoodsByBrand.OrderId, orderId), null);
        if (cNotEngByBrand != null && cNotEngByBrand.moveToFirst()) {
            Set<String> notEnoughByBrand = new LinkedHashSet<>(cNotEngByBrand.getColumnCount());
            do {
                notEnoughByBrand.add(cNotEngByBrand.getString(cNotEngByBrand.getColumnIndex(OrdersNotEnoughGoodsByBrand.Brand)));
            }
            while (cNotEngByBrand.moveToNext());

            forEditOrderTemplate.setNotEnoughByBrand(notEnoughByBrand);
            cNotEngByBrand.close();
        }

        Cursor cNotEngBySeries = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                OrdersNotEnoughGoodsBySeries.Table, OrdersNotEnoughGoodsBySeries.OrderId, orderId), null);
        if (cNotEngBySeries != null && cNotEngBySeries.moveToFirst()) {
            Set<String> notEnoughBySeries = new LinkedHashSet<>(cNotEngBySeries.getColumnCount());
            do {
                notEnoughBySeries.add(cNotEngBySeries.getString(cNotEngBySeries.getColumnIndex(OrdersNotEnoughGoodsBySeries.Series)));
            }
            while (cNotEngBySeries.moveToNext());

            forEditOrderTemplate.setNotEnoughBySeries(notEnoughBySeries);
            cNotEngBySeries.close();
        }

        Cursor cGifts = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                OrdersGiftList.Table, OrdersGiftList.OrderId, orderId), null);
        if (cGifts != null && cGifts.moveToFirst()) {
            LinkedList<GoodsByBrandTemplate> gifts = new LinkedList<>();
            do {
                GoodsByBrandTemplate gift = new GoodsByBrandTemplate();
                gift.setForProduct(cGifts.getString(cGifts.getColumnIndex(OrdersGiftList.ForProduct)));
                gift.setProductCode(cGifts.getString(cGifts.getColumnIndex(OrdersGiftList.ProductCode)));
                gift.setProductName(cGifts.getString(cGifts.getColumnIndex(OrdersGiftList.ProductName)));
                gift.setPrice(Double.parseDouble(cGifts.getString(cGifts.getColumnIndex(OrdersGiftList.Price))));
                gift.setDiscountValue(cGifts.getInt(cGifts.getColumnIndex(OrdersGiftList.Discount)));
                gift.setCount(cGifts.getInt(cGifts.getColumnIndex(OrdersGiftList.Count)));
                gift.setAmount(cGifts.getInt(cGifts.getColumnIndex(OrdersGiftList.Amount)));
                gift.setMinAmount(cGifts.getInt(cGifts.getColumnIndex(OrdersGiftList.MinAmount)));
                gift.setWeight(Double.parseDouble(cGifts.getString(cGifts.getColumnIndex(OrdersGiftList.Weight))));
                gift.setCapacity(Double.parseDouble(cGifts.getString(cGifts.getColumnIndex(OrdersGiftList.Capacity))));
                gift.setAmountBySeries(cGifts.getInt(cGifts.getColumnIndex(OrdersGiftList.AmountBySeries)));
                gifts.add(gift);
            }
            while (cGifts.moveToNext());
            forEditOrderTemplate.setGiftList(gifts);
            cGifts.close();
        }

        Cursor cCreditVisit = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                CreditVisits.Table, CreditVisits.OrderId, orderId), null);
        if (cCreditVisit.moveToFirst()) {
            LinkedList<CreditVisitTemplate> creditVisitList = new LinkedList<>();
            do {
                CreditVisitTemplate creditVisit = new CreditVisitTemplate();
                creditVisit.setVisitDate(cCreditVisit.getString(cCreditVisit.getColumnIndex(CreditVisits.VisitDateView)));
                creditVisit.setVisitDateValue(cCreditVisit.getString(cCreditVisit.getColumnIndex(CreditVisits.VisitDateValue)));
                creditVisit.setTakeSum(cCreditVisit.getString(cCreditVisit.getColumnIndex(CreditVisits.Sum)));
                creditVisitList.add(creditVisit);
            }
            while (cCreditVisit.moveToNext());
            forEditOrderTemplate.setCreditVisits(creditVisitList);
            cCreditVisit.close();
        }

        return forEditOrderTemplate;
    }

    /**
     * Удаляет сохранёный заказ
     *
     * @param orderId ид заказа
     * @return true если заказ был удалён
     */
    public synchronized boolean deleteOrder(long orderId) {
        if (sqLiteDatabase.delete(Orders.Table, String.format("%s = %s", Orders.RowId, orderId), null) != 0) {
            sqLiteDatabase.delete(OrdersGoods.Table, String.format("%s = %s", OrdersGoods.OrderId, orderId), null);
            sqLiteDatabase.delete(OrdersCompetitorScouting.Table, String.format("%s = %s",
                    OrdersCompetitorScouting.OrderId, orderId), null);
            sqLiteDatabase.delete(OrdersNotEnoughGoods.Table, String.format("%s = %s",
                    OrdersNotEnoughGoods.OrderId, orderId), null);
            sqLiteDatabase.delete(OrdersNotEnoughGoodsByBrand.Table, String.format("%s = %s",
                    OrdersNotEnoughGoodsByBrand.OrderId, orderId), null);
            sqLiteDatabase.delete(OrdersNotEnoughGoodsBySeries.Table, String.format("%s = %s",
                    OrdersNotEnoughGoodsBySeries.OrderId, orderId), null);
            sqLiteDatabase.delete(OrdersGiftList.Table, String.format("%s = %s",
                    OrdersGiftList.OrderId, orderId), null);
            sqLiteDatabase.delete(CreditVisits.Table, String.format("%s = %s",
                    CreditVisits.OrderId, orderId), null);
            return true;
        }
        return false;
    }

    /**
     * Возвращает список сохранённых заказов в бд приложения, которые не были отправлены на сервер
     * по причине отсутствия подключения к интернету
     *
     * @return List of generated json objects
     */
    public synchronized ArrayList<MadeOrderTemplate> getOrdersToSend() {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select %s, * from %s where %s = %s",
                Orders.RowId, Orders.Table, Orders.Status, OrderStatus.NeedToSend.getValue()), null);

        ArrayList<WarehouseTemplate> stockTemplates = getWarehouse();

        try {
            if (cursor.moveToFirst()) {
                ArrayList<MadeOrderTemplate> orders = new ArrayList<>(cursor.getCount());
                do {
                    MadeOrderTemplate madeOrder = new MadeOrderTemplate();
                    madeOrder.setRowId(cursor.getInt(0));
                    madeOrder.setOrderType(cursor.getInt(cursor.getColumnIndex(Orders.OrderType)));
                    madeOrder.setOnCredit(Util.IntToBool(cursor.getInt(cursor.getColumnIndex(Orders.OnCredit))));
                    madeOrder.setOrderTitle(cursor.getString(cursor.getColumnIndex(Orders.Title)));
                    madeOrder.setPriceType(cursor.getString(cursor.getColumnIndex(Orders.PriceType)));
                    madeOrder.setPriceTypeName(cursor.getString(cursor.getColumnIndex(Orders.PriceTypeName)));
                    madeOrder.setTotalPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.TotalPrice))));
                    madeOrder.setUserCode(cursor.getString(cursor.getColumnIndex(Orders.UserCode)));
                    madeOrder.setTpCode(cursor.getString(cursor.getColumnIndex(Orders.TradingPoint)));
                    madeOrder.setPriceType(cursor.getString(cursor.getColumnIndex(Orders.PriceType)));
                    madeOrder.setUploadDateView(cursor.getString(cursor.getColumnIndex(Orders.UploadDateView)));
                    madeOrder.setUploadDateValue(cursor.getString(cursor.getColumnIndex(Orders.UploadDateValue)));
                    madeOrder.setCommentTo(cursor.getString(cursor.getColumnIndex(Orders.CommentTo)));
                    madeOrder.setComment(cursor.getString(cursor.getColumnIndex(Orders.Comment)));
                    madeOrder.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.Latitude))));
                    madeOrder.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.Longitude))));
                    madeOrder.setWeight(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.Weight))));
                    madeOrder.setCapacity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Orders.Capacity))));
                    madeOrder.setCreatedDate(Long.parseLong(cursor.getString(cursor.getColumnIndex(Orders.CreatedDate))));
                    for (WarehouseTemplate stockTemplate : stockTemplates) {
                        if (stockTemplate.getCode().equals(cursor.getString(cursor.getColumnIndex(Orders.CodeWarehouse)))) {
                            madeOrder.setStockTemplate(stockTemplate);
                            madeOrder.setOrganizationTemplate(stockTemplate.getOrg());
                            break;
                        }
                    }
                    // * товары по заказу
                    Cursor cGoods = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                            OrdersGoods.Table, OrdersGoods.OrderId, madeOrder.getRowId()), null);
                    if (cGoods != null && cGoods.moveToFirst()) {
                        Map<String, GoodsByBrandTemplate> goodsList = new HashMap<>();
                        do {
                            GoodsByBrandTemplate goodsItem = new GoodsByBrandTemplate();
                            goodsItem.setBrand(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Brand)));
                            goodsItem.setSeries(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Series)));
                            goodsItem.setWarehouseCode(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.WarehouseCode)));
                            goodsItem.setProductCode(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.ProductCode)));
                            goodsItem.setProductName(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.ProductName)));
                            goodsItem.setAmount(cGoods.getInt(cGoods.getColumnIndex(OrdersGoods.Amount)));
                            goodsItem.setPrice(Double.parseDouble(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Price))));
                            goodsItem.setTotal(Double.parseDouble(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Total))));
                            goodsItem.setWeight(Double.parseDouble(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Weight))));
                            goodsItem.setCapacity(Double.parseDouble(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.Capacity))));
                            goodsItem.setMinAmount(cGoods.getInt(cGoods.getColumnIndex(OrdersGoods.MinAmount)));
                            goodsItem.setOriginalPrice(Double.parseDouble(cGoods.getString(cGoods.getColumnIndex(OrdersGoods.OriginalPrice))));
                            goodsItem.setDiscountValue(cGoods.getInt(cGoods.getColumnIndex(OrdersGoods.DiscountValue)));
                            goodsList.put(goodsItem.getProductCode(), goodsItem);
                        }
                        while (cGoods.moveToNext());
                        madeOrder.setGoodsList(goodsList);
                        cGoods.close();
                    }
                    // * информация по конкурентам
                    Cursor cCompetitors = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                            OrdersCompetitorScouting.Table, OrdersCompetitorScouting.OrderId, madeOrder.getRowId()), null);
                    if (cCompetitors != null && cCompetitors.moveToFirst()) {
                        LinkedList<CompetitorScoutingTemplate> competitorItems = new LinkedList<>();
                        do {
                            CompetitorScoutingTemplate competitorItem = new CompetitorScoutingTemplate();
                            competitorItem.setName(cCompetitors.getString(cCompetitors.getColumnIndex(OrdersCompetitorScouting.Name)));
                            competitorItem.setGoods(cCompetitors.getString(cCompetitors.getColumnIndex(OrdersCompetitorScouting.Goods)));
                            competitorItem.setPrice(cCompetitors.getString(cCompetitors.getColumnIndex(OrdersCompetitorScouting.Price)));
                            competitorItems.add(competitorItem);
                        }
                        while (cCompetitors.moveToNext());
                        cCompetitors.close();
                        madeOrder.setCompetitorList(competitorItems);
                    }
                    // * подарки
                    Cursor cGifts = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                            OrdersGiftList.Table, OrdersGiftList.OrderId, madeOrder.getRowId()), null);
                    if (cGifts != null && cGifts.moveToFirst()) {
                        LinkedList<GoodsByBrandTemplate> gifts = new LinkedList<>();
                        do {
                            GoodsByBrandTemplate gift = new GoodsByBrandTemplate();
                            gift.setForProduct(cGifts.getString(cGifts.getColumnIndex(OrdersGiftList.ForProduct)));
                            gift.setProductCode(cGifts.getString(cGifts.getColumnIndex(OrdersGiftList.ProductCode)));
                            gift.setProductName(cGifts.getString(cGifts.getColumnIndex(OrdersGiftList.ProductName)));
                            gift.setPrice(Double.parseDouble(cGifts.getString(cGifts.getColumnIndex(OrdersGiftList.Price))));
                            gift.setDiscountValue(cGifts.getInt(cGifts.getColumnIndex(OrdersGiftList.Discount)));
                            gift.setCount(cGifts.getInt(cGifts.getColumnIndex(OrdersGiftList.Count)));
                            gift.setAmount(cGifts.getInt(cGifts.getColumnIndex(OrdersGiftList.Amount)));
                            gifts.add(gift);
                        }
                        while (cGifts.moveToNext());
                        cGifts.close();
                        madeOrder.setGiftList(gifts);
                    }
                    // * визиты по кредиту
                    Cursor cCreditVisits = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = %s",
                            CreditVisits.Table, CreditVisits.OrderId, madeOrder.getRowId()), null);
                    if (cCreditVisits.moveToFirst()) {
                        LinkedList<CreditVisitTemplate> creditVisitList = new LinkedList<>();
                        do {
                            CreditVisitTemplate creditVisit = new CreditVisitTemplate();
                            creditVisit.setVisitDate(cCreditVisits.getString(cCreditVisits.getColumnIndex(CreditVisits.VisitDateView)));
                            creditVisit.setVisitDateValue(cCreditVisits.getString(cCreditVisits.getColumnIndex(CreditVisits.VisitDateValue)));
                            creditVisit.setTakeSum(cCreditVisits.getString(cCreditVisits.getColumnIndex(CreditVisits.Sum)));
                            creditVisitList.add(creditVisit);
                        }
                        while (cCreditVisits.moveToNext());
                        cCreditVisits.close();
                        madeOrder.setCreditVisits(creditVisitList);
                    }
                    orders.add(madeOrder);
                }
                while (cursor.moveToNext());
                return orders;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Проверяет, все ли заказы были автоотосланы
     *
     * @return false - have been sent;
     */
    public synchronized boolean isHaveToResendOrders() {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select 1 from %s where %s = %s limit 1",
                Orders.Table, Orders.Status, OrderStatus.NeedToSend.getValue()), null);
        try {
            if (cursor != null && cursor.moveToFirst())
                return true;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }

    /**
     * Вызывается в случае акции.
     * Обновляются товары, добавляя информации по акции, в которой за определённое кол-во товаров
     * выдаётся подарок.
     * И обновляются цены, если акция идёт по скидке. Цены обновляются по типу и коду товаров, но могут и все.
     */
    public synchronized void updateProductsAndPrices(SoapObject response) {
        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.delete(GiftList.Table, null, null);
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject mainItem = (SoapObject) response.getProperty(i);
                SoapObject discount = (SoapObject) mainItem.getProperty("Discount");
                //L.info("--------------------------------------------------------");
                //L.info("Discount Info:");
                //L.info(String.format("   code: %s", discount.getProperty("Code")));
                //L.info(String.format("   name: %s", discount.getProperty("Name")));
                //L.info(String.format("   type: %s", discount.getProperty("Type")));

                if (discount.getProperty("Type").toString().equals("1")) {
                    //L.info("++ Скидка ++");
                    //L.info(String.format("Discount by: %s", mainItem.getProperty("DiscountValue").toString() + "%"));
                    float discountValue = Float.parseFloat(mainItem.getProperty("DiscountValue").toString()) / 100;

                    SoapObject forPriceType = (SoapObject) mainItem.getProperty("Filters");
                    SoapObject forProducts = (SoapObject) mainItem.getProperty("ProductCodes");

                    StringBuilder selection = new StringBuilder();
                    selection.append("select * from " + PriceList.Table);

                    if (forPriceType.getPropertyCount() > 0 || forProducts.getPropertyCount() > 0) {
                        selection.append(" where ");
                        // * условие по типам цен
                        if (forPriceType.getPropertyCount() > 0) {
                            selection.append("(");
                            for (int i1 = 0; i1 < forPriceType.getPropertyCount(); i1++)
                                selection.append(String.format("%s = '%s'", PriceList.Code,
                                        ((SoapObject) forPriceType.getProperty(i1)).getProperty("Value"))).append(" or ");
                            selection.delete(selection.length() - 4, selection.length()).append(") and (");
                        }
                        // * условие по кодам товара
                        if (forProducts.getPropertyCount() > 0) {
                            for (int i2 = 0; i2 < forProducts.getPropertyCount(); i2++)
                                selection.append(String.format("%s = '%s'", PriceList.ProductCode,
                                        ((SoapObject) forProducts.getProperty(i2)).getProperty("CodeProduct"))).append(" or ");
                            selection.delete(selection.length() - 4, selection.length()).append(")");
                        }
                    }
                    //L.info(selection.toString());
                    Cursor priceListCursor = sqLiteDatabase.rawQuery(selection.toString(), null);
                    if (priceListCursor != null && priceListCursor.moveToFirst()) {
                        do {
                            int price = Integer.parseInt(priceListCursor.getString(priceListCursor.getColumnIndex(PriceList.Price)));

                            ContentValues cvFields = new ContentValues();
                            cvFields.put(PriceList.NewPrice, price - (price * discountValue));
                            cvFields.put(PriceList.Discount, mainItem.getProperty("DiscountValue").toString());

                            sqLiteDatabase.updateWithOnConflict(PriceList.Table, cvFields, String.format("%s = '%s' and %s = '%s'", PriceList.Code,
                                    priceListCursor.getString(priceListCursor.getColumnIndex(PriceList.Code)),
                                    PriceList.ProductCode, priceListCursor.getString(priceListCursor.getColumnIndex(PriceList.ProductCode))), null, SQLiteDatabase.CONFLICT_REPLACE);
                        }
                        while (priceListCursor.moveToNext());
                        priceListCursor.close();
                    }
                } else {
                    //L.info("++ Акция ++");
                    //L.info(String.format("For every: %s", mainItem.getProperty("AmountMin")));
                    SoapObject forProducts = (SoapObject) mainItem.getProperty("ProductCodes");

                    StringBuilder selection = new StringBuilder();
                    selection.append("select * from ").append(Products.Table);

                    if (forProducts.getPropertyCount() > 0) {
                        selection.append(" where ");
                        for (int i1 = 0; i1 < forProducts.getPropertyCount(); i1++)
                            selection.append(String.format("%s = '%s'", Products.ProductCode,
                                    ((SoapObject) forProducts.getProperty(i1)).getProperty("CodeProduct"))).append(" or ");
                        selection.delete(selection.length() - 4, selection.length());
                    }
                    //L.info(selection.toString());
                    Cursor productsCursor = sqLiteDatabase.rawQuery(selection.toString(), null);
                    if (productsCursor != null && productsCursor.moveToFirst()) {
                        do {
                            ContentValues cvFields = new ContentValues();
                            cvFields.put(Products.MinAmount, mainItem.getProperty("AmountMin").toString());
                            cvFields.put(Products.EventCode, discount.getProperty("Code").toString());

                            sqLiteDatabase.updateWithOnConflict(Products.Table, cvFields, String.format("%s = '%s'", Products.ProductCode,
                                    productsCursor.getString(productsCursor.getColumnIndex(Products.ProductCode))), null, SQLiteDatabase.CONFLICT_REPLACE);

                        }
                        while (productsCursor.moveToNext());
                        productsCursor.close();
                    }

                    // * Подарки по акции
                    //L.info("Gifts:");
                    SoapObject giftList = (SoapObject) mainItem.getProperty("Gifts");
                    for (int i2 = 0; i2 < giftList.getPropertyCount(); i2++) {
                        SoapObject item = (SoapObject) giftList.getProperty(i2);

                        ContentValues cvFields = new ContentValues();
                        cvFields.put(GiftList.EventCode, discount.getProperty("Code").toString());
                        cvFields.put(GiftList.ProductCode, item.getProperty("Code").toString());
                        cvFields.put(GiftList.ProductName, item.getProperty("Name").toString());
                        cvFields.put(GiftList.Amount, item.getProperty("Amount").toString());
                        cvFields.put(GiftList.MinAmount, mainItem.getProperty("AmountMin").toString());

                        sqLiteDatabase.insertWithOnConflict(GiftList.Table, null, cvFields, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception ex) {
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
        //checkTemp();
    }

    private synchronized void checkTemp() {
        L.info("++ Price List ++");
        Cursor c = sqLiteDatabase.rawQuery("select * from " + PriceList.Table, null);
        if (c != null && c.moveToFirst()) {
            do {
                L.info("----------------------------------------------------");
                L.info(String.format("Price Type Code:..%s", c.getString(c.getColumnIndex(PriceList.Code))));
                L.info(String.format("Produce Code:.....%s", c.getString(c.getColumnIndex(PriceList.ProductCode))));
                L.info(String.format("Price:............%s", c.getString(c.getColumnIndex(PriceList.Price))));
                L.info(String.format("New Price:........%s", c.getString(c.getColumnIndex(PriceList.NewPrice))));
                L.info(String.format("Discount:.........%s", c.getString(c.getColumnIndex(PriceList.Discount))));
            }
            while (c.moveToNext());
            c.close();
        }
        L.info("++ Products ++");
        Cursor c1 = sqLiteDatabase.rawQuery("select * from " + Products.Table, null);
        if (c1 != null && c1.moveToFirst()) {
            do {
                L.info("----------------------------------------------------");
                L.info(String.format("WareHouse Code:.%s", c1.getString(c1.getColumnIndex(Products.WarehouseCode))));
                L.info(String.format("Product Code:...%s", c1.getString(c1.getColumnIndex(Products.ProductCode))));
                L.info(String.format("Product Name:...%s", c1.getString(c1.getColumnIndex(Products.ProductName))));
                L.info(String.format("Min Amount:.....%s", c1.getString(c1.getColumnIndex(Products.MinAmount))));
            }
            while (c1.moveToNext());
            c1.close();
        }
        L.info("++ Gifts ++");
        Cursor c2 = sqLiteDatabase.rawQuery("select * from " + GiftList.Table, null);
        if (c2 != null && c2.moveToFirst()) {
            do {
                L.info("----------------------------------------------------");
                L.info(String.format("Event Code:...%s", c2.getString(c2.getColumnIndex(GiftList.EventCode))));
                L.info(String.format("Product Code:.%s", c2.getString(c2.getColumnIndex(GiftList.ProductCode))));
                L.info(String.format("Amount:.......%s", c2.getString(c2.getColumnIndex(GiftList.Amount))));
            }
            while (c2.moveToNext());
            c2.close();
        }
    }

    /**
     * Временное сохранение посылки кассира
     *
     * @param in данные по посылке
     * @return ид записи
     */
    public synchronized long saveTempParcel(ParcelTemplate in) {
        long res;
        ContentValues fields = new ContentValues();
        fields.put(ParcelTemp.BrCode, in.getDealerCode());
        fields.put(ParcelTemp.BrName, in.getDealerName());
        fields.put(ParcelTemp.Comment, in.getComment());
        fields.put(ParcelTemp.CurrencyType, in.getCurrencyType());
        fields.put(ParcelTemp.Summa, in.getSumma());
        fields.put(ParcelTemp.Rate, in.getRate());

        sqLiteDatabase.beginTransaction();
        try {
            if (in.getRowId() != -1) {
                sqLiteDatabase.updateWithOnConflict(ParcelTemp.Table, fields, String.format("%s = %s",
                        ParcelTemp.RowId, in.getRowId()), null, SQLiteDatabase.CONFLICT_REPLACE);
                res = in.getRowId();
            } else
                res = sqLiteDatabase.insertWithOnConflict(ParcelTemp.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return res;
    }

    /**
     * @return возвращает список временно сохранённых посылок
     */
    public synchronized LinkedList<ParcelTemplate> getTempSavedParcels() {
        LinkedList<ParcelTemplate> res = new LinkedList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select %s, * from %s", ParcelTemp.RowId, ParcelTemp.Table),
                null);
        if (cursor.moveToFirst()) {
            do {
                ParcelTemplate item = new ParcelTemplate();
                item.setRowId(cursor.getLong(0));
                item.setDealerCode(cursor.getString(cursor.getColumnIndex(ParcelTemp.BrCode)));
                item.setDealerName(cursor.getString(cursor.getColumnIndex(ParcelTemp.BrName)));
                item.setComment(cursor.getString(cursor.getColumnIndex(ParcelTemp.Comment)));
                item.setSumma(cursor.getString(cursor.getColumnIndex(ParcelTemp.Summa)));
                item.setCurrencyType(cursor.getString(cursor.getColumnIndex(ParcelTemp.CurrencyType)));
                item.setRate(cursor.getString(cursor.getColumnIndex(ParcelTemp.Rate)));
                res.add(item);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return res;
    }

    /**
     * Удаляет временно сохранённую посылку с бд
     *
     * @param rowId ид записи
     */
    public synchronized void deleteTempParcel(long rowId) {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(ParcelTemp.Table, String.format("%s = %s", ParcelTemp.RowId, rowId), null);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Сохраняет на телефоне справочник дилеров кассира
     *
     * @param in данные по дилерам
     */
    public synchronized void saveDealers(SoapObject in) {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(Dealers.Table, null, null);
            final String created_date = System.currentTimeMillis() + "";
            for (int i = 0; i < in.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) in.getProperty(i);
                ContentValues fields = new ContentValues();
                fields.put(Dealers.Code, item.getPropertyAsString("Code"));
                fields.put(Dealers.Name, item.getPropertyAsString("Name"));
                fields.put(Dealers.UpdatedDate, created_date);
                sqLiteDatabase.insertWithOnConflict(Dealers.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Определяет, необходимо ли обновить список дилеров
     */
    public synchronized boolean isNeedUpdDealers() {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select %s from %s limit 1",
                Dealers.UpdatedDate, Dealers.Table), null);
        try {
            if (cursor.moveToFirst()) {
                long last_day = System.currentTimeMillis() - Long.parseLong(cursor.getString(0));
                return (TimeUnit.DAYS.convert(last_day, TimeUnit.MILLISECONDS) >= 7);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return true;
    }

    /**
     * @return возвращает список дилеров кассира
     */
    public synchronized ArrayList<BusinessRegionsTemplate> getDealers() {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", Dealers.Table), null);
        try {
            if (cursor.moveToFirst()) {
                ArrayList<BusinessRegionsTemplate> result = new ArrayList<>(cursor.getCount());
                do {
                    BusinessRegionsTemplate item = new BusinessRegionsTemplate();
                    item.setCode(cursor.getString(cursor.getColumnIndex(Dealers.Code)));
                    item.setName(cursor.getString(cursor.getColumnIndex(Dealers.Name)));
                    result.add(item);
                }
                while (cursor.moveToNext());
                return result;
            } else
                return new ArrayList<>(1);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * Очищает таблицу причин отказов
     */
    private synchronized void clearRefusals() {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(Refusals.Table, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE CLEARING REFUSALS TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Записывает причины отказов
     *
     * @param items - список причин отказов
     */
    public synchronized void saveRefusals(SoapObject items) {
        clearRefusals();
        sqLiteDatabase.beginTransaction();
        try {
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject refusal = (SoapObject) items.getProperty(i);

                ContentValues fields = new ContentValues();
                fields.put(Refusals.Code, refusal.getProperty("Code").toString());
                fields.put(Refusals.Name, refusal.getProperty("Name").toString());

                sqLiteDatabase.insertWithOnConflict(Refusals.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVE REFUSALS TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * @return - список причин отказов
     */
    public synchronized ArrayList<RefusalTemplate> getRefusals() {
        ArrayList<RefusalTemplate> result;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", Refusals.Table), null);
        try {
            if (cursor.moveToFirst()) {
                result = new ArrayList<>(cursor.getCount());
                do {
                    RefusalTemplate item = new RefusalTemplate();
                    item.setCode(cursor.getString(cursor.getColumnIndex(Refusals.Code)));
                    item.setName(cursor.getString(cursor.getColumnIndex(Refusals.Name)));
                    result.add(item);
                }
                while (cursor.moveToNext());
            } else
                result = new ArrayList<>(1);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    public synchronized void saveOrganization(SoapObject items) {
        sqLiteDatabase.beginTransaction();
        clearOrganization();
        try {
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject refusal = (SoapObject) items.getProperty(i);

                ContentValues fields = new ContentValues();
                fields.put(Organization.Code, refusal.getProperty("Code").toString());
                fields.put(Organization.Name, refusal.getProperty("Name").toString());

                sqLiteDatabase.insertWithOnConflict(Organization.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVE ORGANIZATION TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }


    public synchronized ArrayList<OrganizationTemplate> getOrganization() {
        ArrayList<OrganizationTemplate> result;
        ArrayList<WarehouseTemplate> stocks = getCleanWarehouse();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", Organization.Table), null);
        try {
            if (cursor.moveToFirst()) {
                result = new ArrayList<>(cursor.getCount());
                do {
                    OrganizationTemplate item = new OrganizationTemplate();
                    item.setCode(cursor.getString(cursor.getColumnIndex(Organization.Code)));
                    item.setName(cursor.getString(cursor.getColumnIndex(Organization.Name)));
                    for (WarehouseTemplate stockTemplate : stocks) {
                        if (stockTemplate.getOrg_code().equals(item.getCode())) {
                            item.getWarehouses().add(stockTemplate);
                        }
                    }
                    result.add(item);
                }
                while (cursor.moveToNext());
            } else
                result = new ArrayList<>(1);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    public synchronized void clearOrganization() {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(Organization.Table, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE CLEARING ORGANIZATION TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public synchronized WarehouseTemplate getWarehouse(String code) {
        ArrayList<OrganizationTemplate> orgTemplates = getOrganization();

        WarehouseTemplate result = null;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s where %s = '%s'", Stock.Table, Stock.Code, code), null);
        try {
            if (cursor.moveToFirst()) {
                WarehouseTemplate item = new WarehouseTemplate();
                item.setCode(cursor.getString(cursor.getColumnIndex(Stock.Code)));
                item.setName(cursor.getString(cursor.getColumnIndex(Stock.Name)));
                item.setOrg_code(cursor.getString(cursor.getColumnIndex(Stock.CodeOrg)));
                String orgCode = cursor.getString(cursor.getColumnIndex(Stock.CodeOrg));
                for (OrganizationTemplate organizationTemplate : orgTemplates) {
                    if (orgCode.equals(organizationTemplate.getCode())) {
                        item.setOrg(organizationTemplate);
                        break;
                    }
                }
                result = item;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    public synchronized void saveWarehouse(SoapObject items) {
        sqLiteDatabase.beginTransaction();
        clearWarehouse();
        try {
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject refusal = (SoapObject) items.getProperty(i);

                ContentValues fields = new ContentValues();
                fields.put(Stock.Code, refusal.getProperty("Code").toString());
                fields.put(Stock.Name, refusal.getProperty("Name").toString());
                fields.put(Stock.CodeOrg, refusal.getProperty("Organization").toString());

                sqLiteDatabase.insertWithOnConflict(Stock.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVE STOCK TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public synchronized ArrayList<ContractTypeTemplate> getContractTypes() {
        ArrayList<ContractTypeTemplate> result;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", ContractType.Table), null);
        try {
            if (cursor.moveToFirst()) {
                result = new ArrayList<>(cursor.getCount());
                do {
                    ContractTypeTemplate item = new ContractTypeTemplate();
                    item.setName(cursor.getString(cursor.getColumnIndex(ContractType.Name)));
                    boolean shouldEnterPassport = cursor.getString(cursor.getColumnIndex(ContractType.ShouldEnterPassport)).equals("1");
                    item.setShouldPassport(shouldEnterPassport);
                    result.add(item);
                }
                while (cursor.moveToNext());
            } else
                result = new ArrayList<>(1);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        Log.println(Log.DEBUG, "dightmerc", result.toString());

        return result;
    }


    public synchronized void saveContractTypes(SoapObject items) {
        sqLiteDatabase.beginTransaction();
        clearAllContractTypes();
        try {
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject refusal = (SoapObject) items.getProperty(i);

                ContentValues fields = new ContentValues();
                fields.put(ContractType.Name, refusal.getProperty("Name").toString());
                fields.put(ContractType.ShouldEnterPassport, refusal.getProperty("shouldEnterPassport").toString());

                sqLiteDatabase.insertWithOnConflict(ContractType.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVE STOCK TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }

        return;
    }

    public synchronized void saveContracts(SoapObject items, String tpCode) {
        sqLiteDatabase.beginTransaction();
        clearContracts(tpCode);
        try {
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject refusal = (SoapObject) items.getProperty(i);

                ContentValues fields = new ContentValues();
                fields.put(Contracts.ClientTpCode, tpCode);
                fields.put(Contracts.DateOfContract, refusal.getProperty("DateOfContract").toString());
                fields.put(Contracts.CodeContract, refusal.getProperty("CodeContract").toString().replace("anyType{}", ""));
                fields.put(Contracts.SumOfContract, refusal.getProperty("SumOfContract").toString());
                fields.put(Contracts.TermOfContract, refusal.getProperty("TermOfContract").toString());
                fields.put(Contracts.TypeOfContract, refusal.getProperty("TypeContract").toString());

                sqLiteDatabase.insertWithOnConflict(Contracts.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVE STOCK TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public synchronized LinkedList<ContractTemplate> getContracts(String tpCode) {
        LinkedList<ContractTemplate> result = new LinkedList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s where tpCode = '%s'",
                Contracts.Table, tpCode), null);

        SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ContractTemplate contractTemplate = new ContractTemplate(
                            simpleDF.parse(cursor.getString(cursor.getColumnIndex(Contracts.DateOfContract))),
                            cursor.getString(cursor.getColumnIndex(Contracts.CodeContract)),
                            Float.parseFloat(cursor.getString(cursor.getColumnIndex(Contracts.SumOfContract))),
                            simpleDF.parse(cursor.getString(cursor.getColumnIndex(Contracts.TermOfContract))),
                            cursor.getString(cursor.getColumnIndex(Contracts.TypeOfContract)),
                            ""
                    );

                    result.add(contractTemplate);
                }
                while (cursor.moveToNext());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    public synchronized ArrayList<WarehouseTemplate> getCleanWarehouse() {
        ArrayList<WarehouseTemplate> result;


        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", Stock.Table), null);
        try {
            if (cursor.moveToFirst()) {
                result = new ArrayList<>(cursor.getCount());
                do {
                    WarehouseTemplate item = new WarehouseTemplate();
                    item.setCode(cursor.getString(cursor.getColumnIndex(Stock.Code)));
                    item.setName(cursor.getString(cursor.getColumnIndex(Stock.Name)));
                    item.setOrg_code(cursor.getString(cursor.getColumnIndex(Stock.CodeOrg)));
                    result.add(item);
                }
                while (cursor.moveToNext());
            } else
                result = new ArrayList<>(1);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    public synchronized ArrayList<WarehouseTemplate> getWarehouse() {
        ArrayList<OrganizationTemplate> orgTemplates;
        ArrayList<WarehouseTemplate> result;

        orgTemplates = getOrganization();

        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", Stock.Table), null);
        try {
            if (cursor.moveToFirst()) {
                result = new ArrayList<>(cursor.getCount());
                do {
                    WarehouseTemplate item = new WarehouseTemplate();
                    item.setCode(cursor.getString(cursor.getColumnIndex(Stock.Code)));
                    item.setName(cursor.getString(cursor.getColumnIndex(Stock.Name)));
                    item.setOrg_code(cursor.getString(cursor.getColumnIndex(Stock.CodeOrg)));
                    String orgCode = cursor.getString(cursor.getColumnIndex(Stock.CodeOrg));
                    for (OrganizationTemplate organizationTemplate : orgTemplates) {
                        if (orgCode.equals(organizationTemplate.getCode())) {
                            item.setOrg(organizationTemplate);
                            break;
                        }
                    }
                    result.add(item);
                }
                while (cursor.moveToNext());
            } else
                result = new ArrayList<>(1);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    public synchronized void clearWarehouse() {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(Stock.Table, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE CLEARING STOCK TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public synchronized void clearContracts(String tpCode) {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(Contracts.Table, String.format("%s = '%s'", Contracts.ClientTpCode, tpCode), null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE CLEARING STOCK TABLE <---");
            L.exception(ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public synchronized void clearAllContractTypes() {
        try {
            sqLiteDatabase.delete(ContractType.Table, null, null);
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE CLEARING STOCK TABLE <---");
            L.exception(ex);
        } finally {
        }
    }

    // @author MrJ
    public synchronized void savePriceListUpdateTime(SoapObject items) {
        try {
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapPrimitive refusal = (SoapPrimitive) items.getProperty(i);
                if (L.UpdateTimePriceList.equals("") || !L.UpdateTimePriceList.equals(refusal.toString())) {
                    L.oldUpdateTimePriceList = L.UpdateTimePriceList;
                    L.UpdateTimePriceList = refusal.toString();
                    if (sp != null) {
                        sp.edit()
                                .putString(L.keyoldUpdateTimePriceList, sp.getString(L.keyUpdateTimePriceList, ""))
                                .putString(L.keyUpdateTimePriceList, refusal.toString())
                                .apply();
                    }
                } else {
                    L.oldUpdateTimePriceList = L.UpdateTimePriceList;
                    if (sp != null) {
                        sp.edit()
                                .putString(L.keyoldUpdateTimePriceList, sp.getString(L.keyUpdateTimePriceList, ""))
                                .apply();
                    }
                }
            }
        } catch (IllegalStateException ex) {
            L.exception("---> LOCALE SAVE PriceListUpdateTime TABLE <---");
            L.exception(ex);
        }
    }

    public synchronized void saveLastSellDate(int date) {
        try {
            Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s",
                LastSellDate.Table), null);

            int count = cursor.getCount();
            cursor.close();

            if (count != 0){
                sqLiteDatabase.beginTransaction();

                ContentValues fields = new ContentValues();
                fields.put(LastSellDate.DateOfBinned, date);

                sqLiteDatabase.updateWithOnConflict(LastSellDate.Table, fields,"rowid = ?", new String[]{"1"}, SQLiteDatabase.CONFLICT_IGNORE);
                sqLiteDatabase.setTransactionSuccessful();
                Log.println(Log.DEBUG, "dightmerc",  "UPDATED");

            } else {
                sqLiteDatabase.beginTransaction();

                ContentValues fields = new ContentValues();
                fields.put(LastSellDate.DateOfBinned, date);
                sqLiteDatabase.insertWithOnConflict(LastSellDate.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);

                sqLiteDatabase.setTransactionSuccessful();
                Log.println(Log.DEBUG, "dightmerc",  "CREATED");

            }

        } catch (Exception ex) {
            Log.println(Log.DEBUG, "dightmerc",  "---> LOCALE SAVE LastSellDate TABLE <---");
            Log.println(Log.DEBUG, "dightmerc",  ex.getMessage());

        } finally {
            sqLiteDatabase.endTransaction();

        }
    }

    public synchronized int getLastSellDate() {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s",
                LastSellDate.Table), null);
        try {
            if (cursor != null && cursor.moveToFirst())
                if (cursor != null ) {
                    if  (cursor.moveToFirst()) {
                        do {
                            int date_of_binned = cursor.getInt(cursor.getColumnIndex("date_of_binned"));
                            Log.println(Log.DEBUG, "dightmerc",  "LastSellDate date_of_binned: " + date_of_binned);
                            cursor.close();
                            return date_of_binned;
                        }while (cursor.moveToNext());
                    }
                }
            return 0;
//                    TODO
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * Сохраняет данные городов и районов контрактации
     *
     * @param items массив данных по районам/городов
     */
    public synchronized void saveDistrictCities(LinkedList<PS_CitiesDistrictContract> items) {
        clearDistricts();
        try {
            sqLiteDatabase.beginTransaction();

            for (PS_CitiesDistrictContract districtCity : items) {
                ContentValues fields = new ContentValues();
                fields.put(DistrictCity.codeProject, districtCity.getCodeProject());
                fields.put(DistrictCity.NameDistrict, districtCity.getNameDistrict());
                fields.put(DistrictCity.CodeDistrict, districtCity.getCodeDistrict());
                sqLiteDatabase.insertWithOnConflict(DistrictCity.Table, null, fields, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception(">>> SAVE DISTRICT/CITY <<<");
            ex.printStackTrace();
        } finally {

            sqLiteDatabase.endTransaction();
        }
    }

    private synchronized void clearDistricts() {
        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.delete(DistrictCity.Table, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalStateException ex) {
            L.exception(">>> CLEARING DISTRICTS <<<");
            ex.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

//    public synchronized ArrayList<ContractTypeTemplate> getContractTypes() {
//        ArrayList<ContractTypeTemplate> result;
//        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", ContractType.Table), null);
//        try {
//            if (cursor.moveToFirst()) {
//                result = new ArrayList<>(cursor.getCount());
//                do {
//                    ContractTypeTemplate item = new ContractTypeTemplate();
//                    item.setName(cursor.getString(cursor.getColumnIndex(ContractType.Name)));
//                    boolean shouldEnterPassport = cursor.getString(cursor.getColumnIndex(ContractType.ShouldEnterPassport)).equals("1");
//                    item.setShouldPassport(shouldEnterPassport);
//                    result.add(item);
//                }
//                while (cursor.moveToNext());
//            } else
//                result = new ArrayList<>(1);
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        Log.println(Log.DEBUG, "dightmerc", result.toString());
//
//        return result;
//    }

    public synchronized ArrayList<PS_CitiesDistrictContract> getDistricts() {
        ArrayList<PS_CitiesDistrictContract> result;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select * from %s", DistrictCity.Table), null);
        try {
            if (cursor.moveToFirst()) {
                result = new ArrayList<>(cursor.getCount());
                do {
                    PS_CitiesDistrictContract district = new PS_CitiesDistrictContract();
                    district.setNameDistrict(cursor.getString(1));
                    district.setCodeDistrict(cursor.getString(cursor.getColumnIndex(DistrictCity.CodeDistrict)));
                    district.setCodeProject(cursor.getString(cursor.getColumnIndex(DistrictCity.codeProject)));

                    result.add(district);
                }
                while (cursor.moveToNext());
            } else {
                result = new ArrayList<>(1);
            }
        }
        catch (IllegalStateException ex) {
            result = new ArrayList<>(1);

            L.exception(">>> GETTING DISTRICTS <<<");
            ex.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        Log.println(Log.DEBUG, "dightmerc", result.toString());
        return result;
    }

    //

}