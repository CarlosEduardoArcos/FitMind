# 🧪 Guía de Pruebas - Login Flow Restaurado

## 📋 Plan de Pruebas

### 🎯 Casos de Prueba

#### 1️⃣ **Prueba del SplashScreen**

**Objetivo**: Verificar que el SplashScreen funciona correctamente

**Pasos**:
1. Abre la aplicación FitMind
2. Observa la pantalla Splash

**Resultado esperado**:
- ✅ Se muestra el texto "FitMind" en color blanco
- ✅ Aparece una barra de progreso lineal
- ✅ La barra se llena gradualmente (2 segundos aprox)
- ✅ Fondo con color primario del tema
- ✅ Después de ~2.5 segundos, navega automáticamente a Login

**Estado**: ⏳ Pendiente de prueba

---

#### 2️⃣ **Prueba de Login con Credenciales**

**Objetivo**: Verificar que el login básico funciona

**Pasos**:
1. En la pantalla de Login, deja los campos vacíos
2. Presiona "Iniciar sesión"
3. Observa el mensaje Toast

**Resultado esperado**:
- ✅ Muestra Toast: "Completa todos los campos"
- ✅ No navega a ninguna pantalla

**Estado**: ⏳ Pendiente de prueba

---

#### 3️⃣ **Prueba de Login Exitoso**

**Objetivo**: Verificar navegación después de login

**Pasos**:
1. Ingresa cualquier email (ej: "test@test.com")
2. Ingresa cualquier contraseña (ej: "123456")
3. Presiona "Iniciar sesión"

**Resultado esperado**:
- ✅ Muestra Toast: "Inicio de sesión local exitoso ✅"
- ✅ Navega a la pantalla Home
- ✅ No se puede volver a Login con botón atrás

**Estado**: ⏳ Pendiente de prueba

---

#### 4️⃣ **Prueba de Modo Invitado**

**Objetivo**: Verificar que el modo invitado funciona

**Pasos**:
1. En la pantalla de Login, presiona "Entrar como invitado"

**Resultado esperado**:
- ✅ Muestra Toast: "Entrando en modo invitado"
- ✅ Navega a la pantalla Home
- ✅ No se puede volver a Login con botón atrás

**Estado**: ⏳ Pendiente de prueba

---

#### 5️⃣ **Prueba de Campos de Texto**

**Objetivo**: Verificar que los campos funcionan correctamente

**Pasos**:
1. Click en el campo "Correo electrónico"
2. Escribe "usuario@ejemplo.com"
3. Click en el campo "Contraseña"
4. Escribe "mipassword"
5. Observa que la contraseña está oculta

**Resultado esperado**:
- ✅ Se puede escribir en ambos campos
- ✅ La contraseña aparece como puntos/asteriscos
- ✅ El texto del email es visible
- ✅ Ambos campos muestran sus labels correctamente

**Estado**: ⏳ Pendiente de prueba

---

#### 6️⃣ **Prueba de Navegación desde Home**

**Objetivo**: Verificar que después de login, la navegación funciona

**Pasos**:
1. Inicia sesión o entra como invitado
2. Estando en Home, navega a otras pantallas usando la barra inferior
3. Intenta volver a Login

**Resultado esperado**:
- ✅ La navegación entre pantallas funciona
- ✅ No aparece Login en el stack de navegación
- ✅ El botón atrás desde Home sale de la app (o va donde corresponda)

**Estado**: ⏳ Pendiente de prueba

---

#### 7️⃣ **Prueba de Validación de Email Vacío**

**Objetivo**: Verificar validación con solo contraseña

**Pasos**:
1. Deja el campo email vacío
2. Ingresa contraseña "test123"
3. Presiona "Iniciar sesión"

**Resultado esperado**:
- ✅ Muestra Toast: "Completa todos los campos"
- ✅ No navega a Home

**Estado**: ⏳ Pendiente de prueba

---

#### 8️⃣ **Prueba de Validación de Contraseña Vacía**

**Objetivo**: Verificar validación con solo email

**Pasos**:
1. Ingresa email "test@test.com"
2. Deja el campo contraseña vacío
3. Presiona "Iniciar sesión"

**Resultado esperado**:
- ✅ Muestra Toast: "Completa todos los campos"
- ✅ No navega a Home

**Estado**: ⏳ Pendiente de prueba

---

#### 9️⃣ **Prueba de UI en Diferentes Temas**

**Objetivo**: Verificar que el diseño es coherente

**Pasos**:
1. Si hay modo oscuro, actívalo
2. Observa las pantallas Splash y Login
3. Verifica colores y contraste

**Resultado esperado**:
- ✅ Los colores se adaptan al tema
- ✅ Texto legible en ambos modos
- ✅ Material 3 theming aplicado correctamente

**Estado**: ⏳ Pendiente de prueba

---

#### 🔟 **Prueba de Rotación de Pantalla**

**Objetivo**: Verificar que el estado se mantiene

**Pasos**:
1. En Login, ingresa email y contraseña
2. Rota el dispositivo
3. Observa los campos

**Resultado esperado**:
- ⚠️ Los campos mantienen su valor (ideal)
- ℹ️ O se borran pero la app no crashea (aceptable)

**Estado**: ⏳ Pendiente de prueba

---

## 📱 Dispositivos para Probar

- [ ] Emulador Android (API 33+)
- [ ] Dispositivo físico Android
- [ ] Diferentes tamaños de pantalla (tablet)
- [ ] Modo oscuro / claro

---

## 🐛 Reporte de Bugs

Si encuentras algún problema, documéntalo aquí:

### Template de Bug Report

```
**Caso de prueba**: [Número]
**Dispositivo**: [Modelo/Emulador]
**Android Version**: [Versión]
**Descripción del problema**: 
**Pasos para reproducir**:
1. 
2. 
3. 
**Resultado esperado**:
**Resultado actual**:
**Capturas de pantalla**: [Si aplica]
```

---

## ✅ Checklist Final

Antes de considerar completa la restauración:

- [ ] Todos los casos de prueba pasados
- [ ] Build sin errores
- [ ] Sin warnings de linter
- [ ] UI coherente con Material 3
- [ ] Navegación fluida
- [ ] Toast messages funcionando
- [ ] Modo invitado operativo
- [ ] Validación de campos correcta

---

## 🚀 Comandos Útiles

### Compilar y ejecutar
```bash
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

### Limpiar build
```bash
.\gradlew.bat clean
```

### Ver logs
```bash
adb logcat | findstr FitMind
```

---

**Última actualización**: 7 de octubre de 2025  
**Estado general**: ✅ Build exitoso - Listo para pruebas

