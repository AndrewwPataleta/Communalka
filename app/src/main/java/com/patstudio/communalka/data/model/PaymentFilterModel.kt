package com.patstudio.communalka.data.model


data class PaymentFilterModel (
    var date:  androidx.core.util.Pair<Long, Long>? = null,
    var suppliers: Pair<Boolean, ArrayList<Supplier>>,
    var services: Pair<Boolean, ArrayList<Service>>,
//   private val services: Pair<Boolean, ArrayList<PersonalAccount>> ,
    var placement: Pair<Boolean, ArrayList<Placement>>,

    )

