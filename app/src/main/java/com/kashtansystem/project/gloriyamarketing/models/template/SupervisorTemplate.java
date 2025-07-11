package com.kashtansystem.project.gloriyamarketing.models.template;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 04.07.2017.
 * ----------------------------------
 */

public class SupervisorTemplate
{
    private String name = "";
    private ArrayList<AgentTemplate> agents;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<AgentTemplate> getAgents()
    {
        return agents;
    }

    public void setAgents(ArrayList<AgentTemplate> agents)
    {
        this.agents = agents;
    }
}