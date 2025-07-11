package com.kashtansystem.project.gloriyamarketing.models.template

import com.kashtansystem.project.gloriyamarketing.core.SoapProject
import com.kashtansystem.project.gloriyamarketing.utils.UserType

data class UserDetial(
        var codeUser: String,
        var name:String,
        var codeProject: String,
        var userType: UserType,
        var codeSklada: String,
        var soapProject: SoapProject
)