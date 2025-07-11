package com.kashtansystem.project.gloriyamarketing.models.template

data class WarehouseTemplate(
    var code: String = "",
    var name: String = "",
    var org: OrganizationTemplate? = null,
    var org_code: String = ""
)