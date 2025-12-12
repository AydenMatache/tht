package com.example.care

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class InspectionItem(
    val category: String,
    val name: String,
    val status: Status = Status.UNCHECKED
)

enum class Status {
    UNCHECKED, PASSED, FAILED
}

class InspectionViewModel : ViewModel() {

    private val _inspectionItems = mutableStateListOf(
        InspectionItem("Exterior", "Headlights (High/Low) Checked"),
        InspectionItem("Exterior", "Emergency Flashers Operational"),
        InspectionItem("Exterior", "Tire Pressure & Lug Nuts Visual Check"),
        InspectionItem("Interior", "Service Brakes & Parking Brake Tested"),
        InspectionItem("Interior", "Steering Wheel/System Functioning"),
        InspectionItem("Safety", "Fire Extinguisher Charged & Secured"),
        InspectionItem("Safety", "First-Aid Kit Present & Stocked"),
        InspectionItem("Safety", "Emergency Exit Doors Operational"),
        InspectionItem("Fluids", "Fuel Gauge Checked & Adequate"),
        InspectionItem("Fluids", "Engine Oil Level Checked"),
        InspectionItem("Fluids", "Windshield Wipers & Fluid Working"),
        InspectionItem("Secret", "Domer")
    )
    val inspectionItems: List<InspectionItem> = _inspectionItems


    var currentItemIndex = mutableStateOf(0)
        private set

    fun onPassClicked() {
        val index = currentItemIndex.value
        val item = _inspectionItems[index]
        _inspectionItems[index] = item.copy(status = Status.PASSED)
    }

    fun onFailClicked() {
        val index = currentItemIndex.value
        val item = _inspectionItems[index]
        _inspectionItems[index] = item.copy(status = Status.FAILED)
    }

    fun goToNextItem() {
        if (currentItemIndex.value < _inspectionItems.size - 1) {
            currentItemIndex.value++
        }
    }
}
