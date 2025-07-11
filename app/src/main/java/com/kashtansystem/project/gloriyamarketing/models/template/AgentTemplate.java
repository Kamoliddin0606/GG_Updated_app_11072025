package com.kashtansystem.project.gloriyamarketing.models.template;

import android.text.Html;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 04.07.2017.
 * ----------------------------------
 */

public class AgentTemplate
{
    private int agentId = 0;
    private String agentName = "";
    private int amountOfTakenOrders = 0;
    private int amountOfVisitedTp = 0;
    private String commentFromAgent = "";
    private String phoneNumber = "";
    private String lastKnownAddress = "";
    private LatLng coordinates = new LatLng(0, 0);
    private ArrayList<LatLng> listOfVisitedTp;

    public int getAgentId()
    {
        return agentId;
    }

    public void setAgentId(int agentId)
    {
        this.agentId = agentId;
    }

    public String getAgentName()
    {
        return agentName;
    }

    public void setAgentName(String agentName)
    {
        this.agentName = agentName;
    }

    public int getAmountOfTakenOrders()
    {
        return amountOfTakenOrders;
    }

    public void setAmountOfTakenOrders(int amountOfTakenOrders)
    {
        this.amountOfTakenOrders = amountOfTakenOrders;
    }

    public int getAmountOfVisitedTp()
    {
        return amountOfVisitedTp;
    }

    public void setAmountOfVisitedTp(int amountOfVisitedTp)
    {
        this.amountOfVisitedTp = amountOfVisitedTp;
    }

    public String getCommentFromAgent()
    {
        return commentFromAgent;
    }

    public void setCommentFromAgent(String commentFromAgent)
    {
        this.commentFromAgent = commentFromAgent;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getLastKnownAddress()
    {
        return Html.fromHtml(String.format("<u>%s</u>", lastKnownAddress)).toString();
    }

    public void setLastKnownAddress(String lastKnownAddress)
    {
        this.lastKnownAddress = lastKnownAddress;
    }

    public LatLng getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates)
    {
        this.coordinates = coordinates;
    }

    public ArrayList<LatLng> getListOfVisitedTp()
    {
        return listOfVisitedTp;
    }

    public void setListOfVisitedTp(ArrayList<LatLng> listOfVisitedTp)
    {
        this.listOfVisitedTp = listOfVisitedTp;
    }
}