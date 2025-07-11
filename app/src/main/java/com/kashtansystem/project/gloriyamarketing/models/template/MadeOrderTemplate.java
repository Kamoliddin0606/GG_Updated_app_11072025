package com.kashtansystem.project.gloriyamarketing.models.template;

import com.kashtansystem.project.gloriyamarketing.utils.OrderStatus;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by FlameKaf on 11.07.2017.
 * ----------------------------------
 * Шаблок созданного заказа
 */

public class MadeOrderTemplate
{
    // * ид заказа в бд приложения
    private long rowId = -1;
    // * ид заказа в бд сервера
    private String orderCode = "";
    // * код пользователя
    private String userCode = "";
    // * код торговой точки
    private String tpCode = "";
    // * наименование торговой точки
    private String tpName = "";
    // * название заказа
    private String orderTitle = "";
    // * тип цены
    private String priceType = "";
    // * тип цены (наименование)
    private String priceTypeName = "";
    // * дата выгрузки товара для отображения
    private String uploadDateView = "";
    // * дата выгрузки товара для отправки на сервер
    private String uploadDateValue = "";
    // * назначение комментария (supervisor, forwarder)
    private String commentTo = "";
    // * комментарий
    private String comment = "";
    // * список товаров (ключ - код товара; значение - шаблон товара)
    private Map<String, GoodsByBrandTemplate> goodsList = new HashMap<>();
    // * список конкурентов
    private LinkedList<CompetitorScoutingTemplate> competitorList = new LinkedList<>();
    private LinkedList<CreditVisitTemplate> creditVisits = new LinkedList<>();
    // * статус заказа
    private OrderStatus status = OrderStatus.NewOrder;
    // * долгота
    private double latitude = 0;
    // * ширина
    private double longitude = 0;
    // * общий вес заказа
    private double weight = 0;
    // * общий обьём заказа
    private double capacity = 0;
    // * общая стоимость заказа
    private double totalPrice = 0;
    // * дата создания
    private long createdDate = 0;
    // * дата создания заказа, полученная с сервера в формате yyyyddmmhhmmss
    private String createdDateForEdit = "";
    // * сохранить локально или нет
    private boolean saveLocal = false;
    // * фотоотчёт
    private String[] photoRep = {"", "", ""};
    // * в кредит (true/false)
    private boolean onCredit = false;
    // * Список товаров по брендам, которых не хватает на складе
    private Set<String> notEnoughByBrand = new LinkedHashSet<>();
    // * Список товаров по сериям/группам, которых не хватает на складе
    private Set<String> notEnoughBySeries = new LinkedHashSet<>();
    // * Список товаров, которых не хватает на складе
    private Map<String, String> notEnoughGoods = new HashMap<>();
    // * Описание ошибки, возникшей во время отправки заказа
    private String description = "";
    private String info = "";
    private LinkedList<GoodsByBrandTemplate> giftList = new LinkedList<>();
    /* тип заказа 1 - кам; 2 - осдо; 3 - обычный */
    private int orderType = 3;
    // +1.2.2 todo stock
    private WarehouseTemplate stockTemplate = new WarehouseTemplate();
    private OrganizationTemplate organizationTemplate = new OrganizationTemplate();

    private String codeContract = "";

    public String getCodeContract() {
        return codeContract;
    }

    public void setCodeContract(String codeContract) {
        this.codeContract = codeContract;
    }

    public void setPhotoRep(String[] photoRep) {
        this.photoRep = photoRep;
    }

    public WarehouseTemplate getStockTemplate() {
        return stockTemplate;
    }

    public void setStockTemplate(WarehouseTemplate stockTemplate) {
        this.stockTemplate = stockTemplate;
    }

    public OrganizationTemplate getOrganizationTemplate() {
        return organizationTemplate;
    }

    public void setOrganizationTemplate(OrganizationTemplate organizationTemplate) {
        this.organizationTemplate = organizationTemplate;
    }

    public long getRowId()
    {
        return rowId;
    }

    public void setRowId(long rowId)
    {
        this.rowId = rowId;
    }

    public String getUserCode()
    {
        return userCode;
    }

    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }

    public String getTpCode()
    {
        return tpCode;
    }

    public void setTpCode(String tpCode)
    {
        this.tpCode = tpCode;
    }

    public String getOrderTitle()
    {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle)
    {
        this.orderTitle = orderTitle;
    }

    /**
     * @return код тип оплаты
     * */
    public String getPriceType()
    {
        return priceType;
    }

    /**
     * @param priceType код тип оплаты
     * */
    public void setPriceType(String priceType)
    {
        this.priceType = priceType;
    }

    /**
     * @return наименование тип оплаты
     * */
    public String getPriceTypeName()
    {
        return priceTypeName;
    }

    /**
     * @param priceTypeName наименование тип оплаты
     * */
    public void setPriceTypeName(String priceTypeName)
    {
        this.priceTypeName = priceTypeName;
    }

    /**
     * @return Назначение комментария
     * */
    public String getCommentTo()
    {
        return commentTo;
    }

    /**
     * @param commentTo Назначение комментария: супервайзеру (supervisor) или экспедитору (forwarder)
     * */
    public void setCommentTo(String commentTo)
    {
        this.commentTo = commentTo;
    }

    /**
     * @return Комментарий к заказу
     * */
    public String getComment()
    {
        return comment;
    }

    /**
     * @param comment Комментарий к заказу
     * */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * @return Товары заказа
     * */
    public Map<String, GoodsByBrandTemplate> getGoodsList()
    {
        return goodsList;
    }

    /**
     * Загружает все переданные товары
     * @param goodsList товары заказа
     * */
    public void setGoodsList(Map<String, GoodsByBrandTemplate> goodsList)
    {
        if (!goodsList.isEmpty())
        {
            this.goodsList.clear();
            this.goodsList.putAll(goodsList);
        }
    }

    /**
     * Добавляет новые товары к существующим
     * @param goodsList товары заказа
     * */
    public void addGoodsList(Map<String, GoodsByBrandTemplate> goodsList)
    {
        if (!goodsList.isEmpty())
            this.goodsList.putAll(goodsList);
    }

    /**
     * @return Список конкурентов
     * */
    public LinkedList<CompetitorScoutingTemplate> getCompetitorList()
    {
        return competitorList;
    }

    /**
     * @param id Ид конкурента
     * @return возвращает информацию по конкуренту
     * */
    public CompetitorScoutingTemplate getCompetitor(int id)
    {
        return competitorList.get(id);
    }

    /**
     * Добавляет список конкурентов
     * @param competitorList Список конкурентов
     * */
    public void setCompetitorList(LinkedList<CompetitorScoutingTemplate> competitorList)
    {
        this.competitorList = competitorList;
    }

    /**
     * Добавляет нового конкурента
     * @param competitor конкурент
     * */
    public void addNewCompetitor(CompetitorScoutingTemplate competitor)
    {
        competitorList.add(competitor);
    }

    /**
     * Удаляет конкурента из списка
     * @param id ид конкурента
     * */
    public void removeCopetitor(int id)
    {
        competitorList.remove(id);
    }

    public OrderStatus getStatus()
    {
        return status;
    }

    public void setStatus(OrderStatus status)
    {
        this.status = status;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public double getCapacity()
    {
        return capacity;
    }

    public void setCapacity(double capacity)
    {
        this.capacity = capacity;
    }

    public double getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public long getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(long createdDate)
    {
        this.createdDate = createdDate;
    }

    public boolean isSaveLocal()
    {
        return saveLocal;
    }

    public void setSaveLocal(boolean saveLocal)
    {
        this.saveLocal = saveLocal;
    }

    public String getPhoto1()
    {
        return photoRep[0];
    }

    public void setPhoto1(String photo1)
    {
        this.photoRep[0] = photo1;
    }

    public String getPhoto2()
    {
        return photoRep[1];
    }

    public void setPhoto2(String photo2)
    {
        this.photoRep[1] = photo2;
    }

    public String getPhoto3()
    {
        return photoRep[2];
    }

    public void setPhoto3(String photo3)
    {
        this.photoRep[2] = photo3;
    }

    public String[] getPhotoRep()
    {
        return photoRep;
    }

    /**
     * @return В редит или нет
     * */
    public boolean isOnCredit()
    {
        return onCredit;
    }

    /**
     * @param onCredit В кредит
     * */
    public void setOnCredit(boolean onCredit)
    {
        this.onCredit = onCredit;
    }

    public Map<String, String> getNotEnoughGoods()
    {
        return notEnoughGoods;
    }

    public void setNotEnoughGoods(Map<String, String> notEnoughGoods)
    {
        if (!this.notEnoughGoods.isEmpty())
            this.notEnoughGoods.clear();
        this.notEnoughGoods.putAll(notEnoughGoods);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Set<String> getNotEnoughByBrand()
    {
        return notEnoughByBrand;
    }

    public void setNotEnoughByBrand(Set<String> notEnoughByBrand)
    {
        if (!this.notEnoughByBrand.isEmpty())
            this.notEnoughByBrand.clear();
        this.notEnoughByBrand.addAll(notEnoughByBrand);
    }

    public Set<String> getNotEnoughBySeries()
    {
        return notEnoughBySeries;
    }

    public void setNotEnoughBySeries(Set<String> notEnoughBySeries)
    {
        this.notEnoughBySeries = notEnoughBySeries;
    }

    public String getUploadDateView()
    {
        return uploadDateView;
    }

    public void setUploadDateView(String uploadDateView)
    {
        this.uploadDateView = uploadDateView;
    }

    public String getUploadDateValue()
    {
        return uploadDateValue;
    }

    public void setUploadDateValue(String uploadDateValue)
    {
        this.uploadDateValue = uploadDateValue;
    }

    public String getOrderCode()
    {
        return orderCode;
    }

    public void setOrderCode(String orderCode)
    {
        this.orderCode = orderCode;
    }

    public String getCreatedDateForEdit()
    {
        return createdDateForEdit;
    }

    public void setCreatedDateForEdit(String createdDateForEdit)
    {
        this.createdDateForEdit = createdDateForEdit;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public LinkedList<GoodsByBrandTemplate> getGiftList()
    {
        return giftList;
    }

    public void setGiftList(LinkedList<GoodsByBrandTemplate> giftList)
    {
        this.giftList.addAll(giftList);
    }

    public void addGift(GoodsByBrandTemplate item)
    {
        this.giftList.add(item);
    }

    public void clearGiftList()
    {
        this.giftList.clear();
    }

    public LinkedList<CreditVisitTemplate> getCreditVisits()
    {
        return creditVisits;
    }

    public void setCreditVisits(LinkedList<CreditVisitTemplate> creditVisits)
    {
        this.creditVisits = creditVisits;
    }

    public void addCreditVisits(CreditVisitTemplate creditVisit)
    {
        this.creditVisits.add(creditVisit);
    }

    public CreditVisitTemplate getCreditVisit(int id)
    {
        return this.creditVisits.get(id);
    }

    /**
     * @return наименование торговой точки
     */
    public String getTpName()
    {
        return tpName;
    }

    /**
     * @param tpName наименование торговой точки
     */
    public void setTpName(String tpName)
    {
        this.tpName = tpName;
    }

    /**
     * @return тип заказа. 1 - кам; 2 - осдо; 3 - обычный
     */
    public int getOrderType()
    {
        return orderType;
    }

    /**
     * @param orderType тип заказа. 1 - кам; 2 - осдо; 3 - обычный
     */
    public void setOrderType(int orderType)
    {
        this.orderType = orderType;
    }
}