package com.kashtansystem.project.gloriyamarketing.models.template

import java.util.*

data class ContractTemplate(
        var dateOfContact: Date,
        var codeContract: String,
        var sumOfContract: Float,
        var termsOfContract: Date,
        var contractType: String,
        var nameContract: String? = ""
)