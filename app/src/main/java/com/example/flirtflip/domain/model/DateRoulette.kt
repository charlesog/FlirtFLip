package com.example.flirtflip.domain.model

data class DatePlan(
    val emoji: String,
    val title: String,
    val description: String,
    val category: String
)

object DateRoulette {

    private val allPlans = listOf(
        // Gastronomía
        DatePlan("🍕", "Cena italiana", "Una noche con pizza, pasta y buena conversación", "food"),
        DatePlan("☕", "Café y charla", "El clásico que nunca falla para romper el hielo", "food"),
        DatePlan("🍣", "Noche de sushi", "Para los que se atreven con los palillos", "food"),
        DatePlan("🍦", "Heladería", "Un plan dulce y sin presión, perfecto para empezar", "food"),
        DatePlan("🥂", "Cena romántica", "Velas, buena música y mejor compañía", "food"),
        DatePlan("🍺", "Tapas y cañas", "El plan español por excelencia, relajado y divertido", "food"),

        // Cultura
        DatePlan("🎬", "Noche de cine", "Elegid una peli juntos y debatidla después", "culture"),
        DatePlan("🏛️", "Visita a un museo", "Para los que prefieren algo con más sustancia", "culture"),
        DatePlan("🎭", "Obra de teatro", "Un plan diferente que siempre da de qué hablar", "culture"),
        DatePlan("📚", "Librería + café", "Cada uno elige un libro para el otro", "culture"),

        // Aventura
        DatePlan("🧗", "Escalada indoor", "Para romper la tensión con adrenalina", "sport"),
        DatePlan("🚴", "Ruta en bici", "Explorad la ciudad o el campo juntos", "sport"),
        DatePlan("🎳", "Tarde de bowling", "Competitivo, divertido y siempre hay risas", "sport"),
        DatePlan("🏓", "Ping pong", "Rápido, dinámico y muy revelador del carácter", "sport"),
        DatePlan("🧘", "Clase de yoga", "Para los que buscan conexión más allá de lo físico", "sport"),
        DatePlan("🏊", "Piscina o playa", "Si el tiempo acompaña, imposible aburrirse", "sport"),

        // Creatividad
        DatePlan("🎨", "Taller de pintura", "Cread algo juntos y lleváoslo de recuerdo", "art"),
        DatePlan("🍳", "Clase de cocina", "Cocinando juntos se conoce a la gente de verdad", "art"),
        DatePlan("📸", "Sesión de fotos", "Explorad la ciudad buscando el mejor encuadre", "art"),

        // Naturaleza
        DatePlan("🌳", "Picnic en el parque", "Sencillo, íntimo y con posibilidad de siesta", "nature"),
        DatePlan("🌅", "Ver el atardecer", "El plan más romántico con el menor esfuerzo", "nature"),
        DatePlan("🐾", "Visita al zoo", "Divertido, relajado y siempre hay conversación", "nature"),

        // Indoor
        DatePlan("🎮", "Tarde de videojuegos", "Para los que saben que el ocio digital une", "gaming"),
        DatePlan("🧩", "Escape room", "La prueba definitiva del trabajo en equipo", "gaming"),
        DatePlan("🎲", "Noche de juegos de mesa", "Competitividad sana y muchas risas garantizadas", "gaming"),
    )

    // Sugerimos planes según hobbies en común, o random si no hay coincidencias
    fun suggest(myHobbies: List<Hobby>, theirHobbies: List<Hobby>): List<DatePlan> {
        val commonHobbies = myHobbies.intersect(theirHobbies.toSet())

        val preferredCategories = commonHobbies.flatMap { hobby ->
            when (hobby) {
                Hobby.SPORTS, Hobby.GYM -> listOf("sport")
                Hobby.MUSIC, Hobby.ART -> listOf("art", "culture")
                Hobby.TRAVEL -> listOf("nature", "culture")
                Hobby.COOKING -> listOf("food")
                Hobby.GAMING -> listOf("gaming")
                Hobby.MOVIES -> listOf("culture")
                Hobby.READING -> listOf("culture")
            }
        }.toSet()

        val preferred = if (preferredCategories.isNotEmpty()) {
            allPlans.filter { it.category in preferredCategories }.shuffled()
        } else emptyList()

        val others = allPlans.filter { it !in preferred }.shuffled()

        return (preferred + others).take(5)
    }

    fun random(): DatePlan = allPlans.random()
}