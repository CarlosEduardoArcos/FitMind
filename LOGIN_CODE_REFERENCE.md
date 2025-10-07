# 💻 Referencia de Código - Login Flow

## 📄 Código Completo de Archivos Modificados

### 1️⃣ LoginScreen.kt

```kotlin
package com.example.fitmind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Bienvenido a FitMind", style = MaterialTheme.typography.headlineSmall)
            Text("Inicia sesión para continuar", style = MaterialTheme.typography.bodyMedium)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        Toast.makeText(context, "Inicio de sesión local exitoso ✅", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }

            TextButton(
                onClick = {
                    Toast.makeText(context, "Entrando en modo invitado", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            ) {
                Text("Entrar como invitado")
            }
        }
    }
}
```

**Características clave**:
- ✅ `remember { mutableStateOf("") }` para estado local
- ✅ `LocalContext.current` para Toast messages
- ✅ `PasswordVisualTransformation()` para ocultar contraseña
- ✅ `popUpTo("login") { inclusive = true }` para limpiar stack de navegación
- ✅ Validación simple con `isNotBlank()`

---

### 2️⃣ SplashScreen.kt

```kotlin
package com.example.fitmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            progress += 0.02f
            delay(40)
        }
        delay(500)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("FitMind", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(
                progress = { progress },
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .width(200.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}
```

**Características clave**:
- ✅ `LaunchedEffect(Unit)` para ejecución única al iniciar
- ✅ Loop para animar el progreso de 0f a 1f
- ✅ `delay(40)` para 40ms entre incrementos (suave)
- ✅ `delay(500)` extra antes de navegar
- ✅ Navegación automática sin interacción del usuario

---

### 3️⃣ Navigation.kt

```kotlin
package com.example.fitmind

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitmind.ui.screens.AddHabitScreen
import com.example.fitmind.ui.screens.DashboardsScreen
import com.example.fitmind.ui.screens.HomeScreen
import com.example.fitmind.ui.screens.LoginScreen
import com.example.fitmind.ui.screens.SettingsScreen
import com.example.fitmind.ui.screens.SplashScreen
import com.example.fitmind.viewmodel.ChartViewModel
import com.example.fitmind.viewmodel.HabitViewModel

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController, viewModel<HabitViewModel>()) }
        composable("addHabit") { AddHabitScreen(navController, viewModel<HabitViewModel>()) }
        composable("dashboards") { DashboardsScreen(navController, viewModel<ChartViewModel>()) }
        composable("settings") { SettingsScreen(navController) }
    }
}
```

**Características clave**:
- ✅ `startDestination = "splash"` define punto de entrada
- ✅ Cada ruta es un simple `composable("ruta") { Pantalla() }`
- ✅ ViewModels inyectados donde se necesitan
- ✅ Sin animaciones para mejor rendimiento

---

## 🔧 Patrones de Código Utilizados

### Patrón 1: Estado Local con Remember

```kotlin
var email by remember { mutableStateOf("") }
```

**Uso**: Almacenar estado que solo vive en el Composable

---

### Patrón 2: Navegación con Limpieza de Stack

```kotlin
navController.navigate("home") {
    popUpTo("login") { inclusive = true }
}
```

**Uso**: Navegar y remover pantallas anteriores del stack

---

### Patrón 3: LaunchedEffect para Efectos Secundarios

```kotlin
LaunchedEffect(Unit) {
    // Código que se ejecuta una vez
    delay(1000)
    // Más código
}
```

**Uso**: Ejecutar código asíncrono cuando el Composable se monta

---

### Patrón 4: Toast Messages

```kotlin
val context = LocalContext.current
Toast.makeText(context, "Mensaje", Toast.LENGTH_SHORT).show()
```

**Uso**: Mostrar mensajes breves al usuario

---

### Patrón 5: Validación Simple

```kotlin
if (email.isNotBlank() && password.isNotBlank()) {
    // Proceder
} else {
    // Mostrar error
}
```

**Uso**: Validar campos antes de procesar

---

## 🎨 Componentes Material 3 Usados

### Box
```kotlin
Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
) { /* contenido */ }
```
**Propósito**: Contenedor que permite centrar contenido

---

### Column
```kotlin
Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
) { /* elementos */ }
```
**Propósito**: Apilar elementos verticalmente con espaciado

---

### OutlinedTextField
```kotlin
OutlinedTextField(
    value = email,
    onValueChange = { email = it },
    label = { Text("Label") },
    modifier = Modifier.fillMaxWidth()
)
```
**Propósito**: Campo de entrada con borde

---

### Button
```kotlin
Button(
    onClick = { /* acción */ },
    modifier = Modifier.fillMaxWidth()
) { Text("Texto") }
```
**Propósito**: Botón principal de acción

---

### TextButton
```kotlin
TextButton(onClick = { /* acción */ }) {
    Text("Texto")
}
```
**Propósito**: Botón secundario sin fondo

---

### LinearProgressIndicator
```kotlin
LinearProgressIndicator(
    progress = { progress },
    color = Color.White,
    modifier = Modifier.width(200.dp)
)
```
**Propósito**: Barra de progreso lineal

---

## 🎯 Modificadores Importantes

| Modificador | Uso | Ejemplo |
|-------------|-----|---------|
| `fillMaxSize()` | Ocupa todo el espacio disponible | `Modifier.fillMaxSize()` |
| `fillMaxWidth()` | Ocupa todo el ancho | `Modifier.fillMaxWidth()` |
| `padding(24.dp)` | Añade espacio alrededor | `Modifier.padding(24.dp)` |
| `background(color)` | Cambia color de fondo | `Modifier.background(Color.Red)` |
| `clip(shape)` | Recorta según forma | `Modifier.clip(RoundedCornerShape(50))` |

---

## 📦 Imports Necesarios

### Para LoginScreen:
```kotlin
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
```

### Para SplashScreen:
```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
```

---

## 💡 Tips y Mejores Prácticas

### ✅ DO (Hacer)
1. Usar `remember` para estado local
2. Validar entradas antes de procesarlas
3. Limpiar stack de navegación con `popUpTo`
4. Usar Material 3 theming
5. Mantener Composables simples y enfocados

### ❌ DON'T (No hacer)
1. No usar ViewModels innecesariamente
2. No hacer lógica compleja en el UI
3. No olvidar validación de campos
4. No dejar pantallas en el stack innecesariamente
5. No mezclar estilos de diferentes versiones de Material

---

**Documentación creada**: 7 de octubre de 2025  
**Versión**: 1.0  
**Estado**: ✅ Completo

