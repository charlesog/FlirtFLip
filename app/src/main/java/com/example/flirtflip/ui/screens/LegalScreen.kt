package com.example.flirtflip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flirtflip.ui.theme.Spacing
import com.example.flirtflip.ui.theme.onBackground
import com.example.flirtflip.ui.theme.onSurfaceVariant
import com.example.flirtflip.ui.theme.primary
import com.example.flirtflip.ui.theme.softPinkSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Aviso Legal", "Privacidad")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Información Legal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = onBackground
                )
            )
        },
        containerColor = softPinkSurface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFF0F7), Color(0xFFFFE4F0))
                    )
                )
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            },
                            selectedContentColor = primary,
                            unselectedContentColor = onSurfaceVariant
                        )
                    }
                }

                // Contenido
                when (selectedTab) {
                    0 -> LegalNoticeContent()
                    1 -> PrivacyPolicyContent()
                }
            }
        }
    }
}

@Composable
private fun LegalNoticeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        LegalCard(
            title = "1. Datos del Titular",
            body = """
                En cumplimiento de la Ley 34/2002, de 11 de julio, de Servicios de la Sociedad de la Información y del Comercio Electrónico (LSSICE), se informa:

                • Denominación social: FlirtFlip App (Proyecto TFG)
                • Responsable: [Nombre del autor del TFG]
                • Centro educativo: [Universidad / Centro]
                • Correo electrónico de contacto: flirtflip@example.com
                • Naturaleza: Aplicación académica sin fines comerciales
            """.trimIndent()
        )

        LegalCard(
            title = "2. Objeto y Finalidad",
            body = """
                FlirtFlip es una aplicación móvil desarrollada exclusivamente con fines académicos como Trabajo de Fin de Grado (TFG). No tiene carácter comercial ni genera beneficio económico de ningún tipo.

                Su finalidad es demostrar competencias técnicas en el desarrollo de aplicaciones Android mediante tecnologías como Kotlin, Jetpack Compose y Firebase.
            """.trimIndent()
        )

        LegalCard(
            title = "3. Propiedad Intelectual",
            body = """
                El código fuente, diseño visual y contenidos de FlirtFlip son obra del autor del TFG y están protegidos por la legislación española e internacional sobre propiedad intelectual.

                Queda prohibida su reproducción, distribución o comunicación pública sin autorización expresa del titular.
            """.trimIndent()
        )

        LegalCard(
            title = "4. Responsabilidad",
            body = """
                El titular no se hace responsable de los posibles daños derivados del uso de la aplicación en entornos de producción, ya que se trata de un prototipo académico.

                Los datos utilizados durante las pruebas son ficticios y cualquier coincidencia con personas reales es puramente casual.
            """.trimIndent()
        )

        LegalCard(
            title = "5. Legislación Aplicable",
            body = """
                Este aviso legal se rige por la legislación española. Para cualquier controversia derivada del uso de la aplicación, las partes se someten a los juzgados y tribunales competentes según la normativa vigente.

                Última actualización: mayo de 2026.
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(Spacing.large))
    }
}

@Composable
private fun PrivacyPolicyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        LegalCard(
            title = "1. Responsable del Tratamiento",
            body = """
                De conformidad con el Reglamento (UE) 2016/679 (RGPD) y la Ley Orgánica 3/2018 (LOPDGDD):

                • Responsable: [Nombre del autor del TFG]
                • Contacto: flirtflip@example.com
                • Finalidad: Proyecto académico TFG — sin tratamiento comercial de datos.
            """.trimIndent()
        )

        LegalCard(
            title = "2. Datos que Recogemos",
            body = """
                Durante el uso de la aplicación se pueden recopilar los siguientes datos:

                • Correo electrónico y contraseña (autenticación vía Firebase Auth)
                • Nombre, edad, género y biografía (perfil de usuario)
                • Fotografías de perfil y galería personal
                • Ubicación aproximada (para mostrar usuarios cercanos)
                • Mensajes enviados en el chat interno
            """.trimIndent()
        )

        LegalCard(
            title = "3. Finalidad del Tratamiento",
            body = """
                Los datos se utilizan exclusivamente para:

                • Permitir el acceso y uso de la aplicación
                • Mostrar el perfil del usuario a otros participantes
                • Calcular la proximidad geográfica entre usuarios
                • Facilitar la comunicación entre usuarios con match mutuo

                En ningún caso se utilizarán para fines publicitarios ni se cederán a terceros.
            """.trimIndent()
        )

        LegalCard(
            title = "4. Base Legal",
            body = """
                El tratamiento de datos se basa en el consentimiento expreso del usuario al registrarse en la aplicación (Art. 6.1.a RGPD).

                El usuario puede retirar su consentimiento en cualquier momento, lo que implicará la eliminación de su cuenta y datos asociados.
            """.trimIndent()
        )

        LegalCard(
            title = "5. Conservación de Datos",
            body = """
                Los datos se conservan mientras el usuario mantenga una cuenta activa en la aplicación.

                Al tratarse de un proyecto académico, la base de datos podrá ser eliminada en su totalidad una vez concluida la evaluación del TFG.
            """.trimIndent()
        )

        LegalCard(
            title = "6. Derechos del Usuario",
            body = """
                El usuario tiene derecho a:

                • Acceder a sus datos personales
                • Rectificar datos inexactos
                • Solicitar la supresión de sus datos
                • Oponerse al tratamiento
                • Portabilidad de sus datos

                Para ejercer estos derechos, contacta en: flirtflip@example.com

                También puedes presentar una reclamación ante la Agencia Española de Protección de Datos (www.aepd.es).
            """.trimIndent()
        )

        LegalCard(
            title = "7. Seguridad",
            body = """
                FlirtFlip aplica medidas técnicas de seguridad sobre la infraestructura de Firebase (Google LLC), que cumple con el RGPD. Las contraseñas se almacenan con hash y nunca en texto plano.

                Última actualización: mayo de 2026.
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(Spacing.large))
    }
}

@Composable
private fun LegalCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = primary,
                modifier = Modifier.padding(bottom = Spacing.small)
            )
            Text(
                text = body,
                fontSize = 13.sp,
                color = onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}