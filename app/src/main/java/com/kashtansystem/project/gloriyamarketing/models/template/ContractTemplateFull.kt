package com.kashtansystem.project.gloriyamarketing.models.template

import java.util.*

data class ContractTemplateFull(
        var dateOfContact: Date,
        var codeContract: String,
        var sumOfContract: Float,
        var termsOfContract: Date,
        var contractType: String,
        var numbReference: String,
        var numbCertificate: String,
        var termReference: Date,
        var termCertificate: Date,
        var numbPassport: String,
        var termPassport: Date,
        var isUnlinTermCertificate: Boolean
)