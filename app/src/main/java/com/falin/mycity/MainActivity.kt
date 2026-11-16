package com.falin.mycity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.falin.mycity.ui.theme.MyCityTheme

data class Place(val name: String, val category: String, val address: String, val description: String, val rating: String)

object CityData {
    val categories = listOf("Кофейни", "Рестораны", "Парки", "Культура")
    val places = listOf(
        Place("Кофемания на Арбате", "Кофейни", "ул. Арбат,  кофейный квартал", "Уютная кофейня в центре с авторскими напитками и десертами. Хорошее место для неспешного утра.", "4,8"),
        Place("Surf Coffee", "Кофейни", "ул. Большая Никитская, 22", "Спешелти кофе, спокойная музыка и удобные места для работы.", "4,7"),
        Place("Дом 12", "Кофейни", "Берсеневская набережная, 6", "Кофейня с видом на набережную и сезонным меню.", "4,6"),
        Place("Тесто и тесто", "Рестораны", "ул. Покровка, 8", "Итальянская кухня, свежая паста и пицца из печи.", "4,7"),
        Place("Чайхона №1", "Рестораны", "ул. Новый Арбат, 21", "Блюда восточной кухни и просторная летняя веранда.", "4,5"),
        Place("Мария Санта", "Рестораны", "ул. Мясницкая, 24", "Небольшое место с домашней кухней и сезонными продуктами.", "4,6"),
        Place("Парк Горького", "Парки", "ул. Крымский Вал, 9", "Прогуляйтесь вдоль Москвы-реки, загляните в Нескучный сад или возьмите велосипед напрокат.", "4,9"),
        Place("Зарядье", "Парки", "ул. Варварка, 6", "Современный парк рядом с Красной площадью со смотровым мостом и разными природными зонами.", "4,8"),
        Place("Аптекарский огород", "Парки", "просп. Мира, 26, стр. 1", "Исторический ботанический сад с оранжереями и сезонными выставками.", "4,8"),
        Place("Третьяковская галерея", "Культура", "Лаврушинский пер., 10", "Главное собрание русского искусства: от древних икон до живописи XX века.", "4,9"),
        Place("Музей космонавтики", "Культура", "просп. Мира, 111", "Интерактивная экспозиция об истории освоения космоса.", "4,7"),
        Place("Большой театр", "Культура", "Театральная площадь, 1", "Легендарная сцена оперы и балета в историческом центре Москвы.", "4,9")
    )
}

class CityViewModel : ViewModel() {
    var category by mutableStateOf<String?>(null); private set
    var selected by mutableStateOf<Place?>(null); private set
    fun openCategory(value: String) { category = value; selected = null }
    fun openPlace(value: Place) { selected = value }
    fun back() { if (selected != null) selected = null else category = null }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MyCityTheme { CityApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityApp(vm: CityViewModel = viewModel()) {
    val place = vm.selected
    val category = vm.category
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(place?.name ?: category ?: "Москва: рядом и интересно", fontWeight = FontWeight.Bold) },
            navigationIcon = { if (category != null) TextButton(onClick = vm::back) { Text("‹ Назад") } }
        )
    }) { padding ->
        when {
            place != null -> PlaceDetail(place, Modifier.padding(padding))
            category != null -> PlaceList(category, vm, Modifier.padding(padding))
            else -> CategoryList(vm, Modifier.padding(padding))
        }
    }
}

@Composable
private fun CategoryList(vm: CityViewModel, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Идеи для прогулок, встреч и открытий в Москве", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 4.dp)) }
        items(CityData.categories) { category ->
            ElevatedCard(onClick = { vm.openCategory(category) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Column { Text(category, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold); Text("3 рекомендации", style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }
}

@Composable
private fun PlaceList(category: String, vm: CityViewModel, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Избранные места Москвы", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 4.dp)) }
        items(CityData.places.filter { it.category == category }) { place ->
            ElevatedCard(onClick = { vm.openPlace(place) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text(place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("★ ${place.rating}  ·  ${place.address}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(place.description, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun PlaceDetail(place: Place, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(24.dp)) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth().height(190.dp)) {
            Box(contentAlignment = Alignment.Center) { Text("📍", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary) }
        }
        Spacer(Modifier.height(20.dp))
        Text(place.category.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(place.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text("★ ${place.rating}", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(14.dp))
        Text(place.description, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(20.dp))
        Text("Адрес", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(place.address, style = MaterialTheme.typography.bodyLarge)
    }
}
