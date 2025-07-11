package com.kashtansystem.project.gloriyamarketing.models.template

data class OrganizationTemplate(
        var code: String = "",
        var name: String = "",
        var warehouses: ArrayList<WarehouseTemplate> = ArrayList()
)