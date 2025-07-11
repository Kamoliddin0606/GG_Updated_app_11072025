package com.kashtansystem.project.gloriyamarketing.models.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by FlameKaf on 20.06.2017.
 * ----------------------------------
 * Макет данных по доставке заказа
 */

public class DeliveryTemplate
{
    private int action = -1;
    private String orderCode = "";
    private String orderCreatedDate = "";
    private String tpCode = "";
    private String tpName = "";
    private String deliveryDate = "dd.mm.yyyy";
    private String address = "";
    private double latitude = 0;
    private double longitude = 0;
    private String refPoint = "";
    private String contactPerson = "";
    private String contactPersonPhone = "";
    private String agent = "";
    private String agentPhone = "";
    private double capacity = 0;
    private double weight = 0;
    private boolean isNeedTakeCash = false;
    private double cashToTake = 0;
    private String reason = "";
    private long deliveryDateLong = 0;
    private ArrayList<GoodsByBrandTemplate> goodsList = null;
    private Map<String, GoodsByBrandTemplate> goodsForReturn = new HashMap<>();
    private int status = 0;

    private boolean chequeSent = false;

    private String chequeStatus = "";

    public String getOrderCode()
    {
        return orderCode;
    }

    public void setOrderCode(String orderCode)
    {
        this.orderCode = orderCode;
    }

    public String getOrderCreatedDate()
    {
        return orderCreatedDate;
    }

    public void setOrderCreatedDate(String orderCreatedDate)
    {
        this.orderCreatedDate = orderCreatedDate;
    }

    public String getTpCode()
    {
        return tpCode;
    }

    public void setTpCode(String tpCode)
    {
        this.tpCode = tpCode;
    }

    public String getTpName()
    {
        return tpName;
    }

    public void setTpName(String tpName)
    {
        this.tpName = tpName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
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

    public String getRefPoint()
    {
        return refPoint;
    }

    public void setRefPoint(String refPoint)
    {
        this.refPoint = refPoint;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPersonPhone()
    {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone)
    {
        this.contactPersonPhone = contactPersonPhone;
    }

    public String getAgent()
    {
        return agent;
    }

    public void setAgent(String agent)
    {
        this.agent = agent;
    }

    public String getAgentPhone()
    {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone)
    {
        this.agentPhone = agentPhone;
    }

    public double getCapacity()
    {
        return capacity;
    }

    public void setCapacity(double capacity)
    {
        this.capacity = capacity;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public ArrayList<GoodsByBrandTemplate> getGoodsList()
    {
        return goodsList;
    }

    public void setGoodsList(ArrayList<GoodsByBrandTemplate> goodsList)
    {
        this.goodsList = new ArrayList<>(goodsList.size());
        this.goodsList.addAll(goodsList);
    }

    public Map<String, GoodsByBrandTemplate> getGoodsForReturn()
    {
        return goodsForReturn;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public double getCashToTake()
    {
        return cashToTake;
    }

    public void setCashToTake(double cashToTake)
    {
        this.cashToTake = cashToTake;
    }

    public int getAction()
    {
        return action;
    }

    public void setAction(int action)
    {
        this.action = action;
    }

    /**
     * @return true, если необходимо взять деньги за заказ у т.т., в противно случае false
     */
    public boolean isNeedTakeCash()
    {
        return isNeedTakeCash;
    }

    /**
     * Сведения о том, нужно ли экспедитору взять деньги у т.т. за заказ
     * @param isNeedTakeCash true, если необходимо, в противно случае false
     */
    public void setIsNeedTakeCash(boolean isNeedTakeCash)
    {
        this.isNeedTakeCash = isNeedTakeCash;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getDeliveryDate()
    {
        return deliveryDate;
    }


    public void setDeliveryDate(String deliveryDate)
    {
        this.deliveryDate = deliveryDate;
    }

    public long getDeliveryDateLong() {
        return deliveryDateLong;
    }

    public void setDeliveryDateLong(long deliveryDateLong) {
        this.deliveryDateLong = deliveryDateLong;
    }

    public boolean getChequeSent() { return chequeSent; }

    public void setChequeSent(boolean chequeSent) { this.chequeSent = chequeSent; }

    public String getChequeStatus() { return chequeStatus; }

    public void setChequeStatus(String chequeStatus) { this.chequeStatus = chequeStatus; }
}
