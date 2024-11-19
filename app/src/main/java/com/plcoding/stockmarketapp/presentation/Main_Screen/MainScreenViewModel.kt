package com.plcoding.stockmarketapp.presentation.Main_Screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.data.csv.CompanyListingsParser
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream

class MainScreenViewModel {


    class MainScreenViewModel(
        private val companyListingsParser: CompanyListingsParser,
        private val csvInputStream: InputStream // Inject the CSV file's input stream
    ) : ViewModel() {

        private val _companyList = MutableStateFlow<List<CompanyListing>>(emptyList())
        val companyList: StateFlow<List<CompanyListing>> = _companyList

        init {
            loadCompanyListings()
        }

        private fun loadCompanyListings() {
            viewModelScope.launch {
                val parsedList = companyListingsParser.parse(csvInputStream)
                _companyList.value = parsedList
            }
        }
    }


}