package com.example.flirtflip

import com.example.flirtflip.domain.model.*
import com.example.flirtflip.domain.repository.*
import com.example.flirtflip.domain.usecase.RegisterUserUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RegisterUserDebugFlowTest {

    private val userRepository = mockk<UserRepository>()
    private val authRepository = mockk<AuthRepository>()
    private val securityProvider = mockk<SecurityProvider>()

    @Test
    fun `ver flujo completo de registro y cifrado con pepper`() = runBlocking {
        // 1. DATOS DE ENTRADA (Lo que el usuario escribe en la primera pantalla)
        val emailRegistro = "usuario@flirtflip.com"
        val passwordOriginal = "mi_clave_secreta_123"

        // 2. VALORES ESPERADOS (Lo que queremos ver tras el proceso)
        val idGeneradoPorFirebase = "FIREBASE_USER_ABC_123"
        val passwordConPepperYHash = "HASH_SEGURO_CON_PIMIENTA_BCRYPT_999"

        // Configuramos el comportamiento de los "puertos" (Mocks)
        coEvery { authRepository.signUp(emailRegistro, passwordOriginal) } returns Result.success(
            idGeneradoPorFirebase
        )
        coEvery { securityProvider.hashPassword(passwordOriginal) } returns passwordConPepperYHash

        // Capturamos el objeto User final que se enviará a Firestore
        val capturadorUsuario = slot<User>()
        coEvery { userRepository.saveUser(capture(capturadorUsuario)) } returns Result.success(Unit)

        val useCase = RegisterUserUseCase(userRepository, securityProvider, authRepository)

        // 3. EJECUCIÓN (Llamamos solo con lo que tenemos al registrar)
        // NOTA: Si tu UseCase aún pide el objeto User, pásale uno básico
        val userBase = User(
            id = "", email = emailRegistro, password = "", profile = UserProfile(
                name = "", firstSurname = "", secondSurname = "", age = 0,
                gender = Gender.MALE, bio = "", hobbies = emptyList(), galerryUrls = emptyList()
            )
        )

        useCase(userBase.email, passwordOriginal)

        // 4. VERIFICACIÓN DEL RESULTADO TRAS LA TRANSFORMACIÓN
        val usuarioFinal = capturadorUsuario.captured

        println("--- RESULTADO DEL FLUJO (HEXAGONAL) ---")
        println("1. El ID capturado de Firebase es: ${usuarioFinal.id}")
        println("2. La Password almacenada es: ${usuarioFinal.password}")
        println("3. El Email se mantiene: ${usuarioFinal.email}")

        // Comprobamos que el objeto final tiene el ID de Firebase y el Hash con Pepper
        assertEquals(idGeneradoPorFirebase, usuarioFinal.id)
        assertEquals(passwordConPepperYHash, usuarioFinal.password)
    }
}