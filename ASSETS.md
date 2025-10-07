# FitMind - Guía de Recursos Visuales

## 📁 Estructura de Archivos

```
app/src/main/res/drawable/
├── fitmind_logo.xml          # Logo principal (512x512)
├── fitmind_splash.xml        # Logo para splash screen (256x256)
├── ic_launcher_foreground.xml # Icono de la app (108x108)
├── icon_home.xml             # Ícono de inicio (128x128)
├── icon_charts.xml           # Ícono de gráficos (128x128)
├── icon_settings.xml         # Ícono de configuración (128x128)
├── icon_add.xml              # Ícono de agregar (128x128)
└── icon_guest.xml            # Ícono de invitado (128x128)

fitmind_banner.svg            # Banner para README (1000x300)
```

## 🎨 Especificaciones de Diseño

### Paleta de Colores
- **Azul Fit**: `#3A86FF` - Color primario
- **Verde Fit**: `#06D6A0` - Color secundario
- **Naranja Fit**: `#FF6B35` - Color de acento
- **Gris Suave**: `#F5F5F5` - Color de fondo
- **Blanco**: `#FFFFFF` - Color de texto/contraste

### Tipografía
- **Fuente Principal**: Inter, sans-serif
- **Fuente Alternativa**: Poppins, sans-serif
- **Pesos**: 400 (Regular), 500 (Medium), 600 (SemiBold), 700 (Bold), 800 (ExtraBold)

### Estilo Visual
- **Esquinas**: Redondeadas (border-radius: 4-16px)
- **Sombras**: Suaves y sutiles
- **Estilo**: Flat design con elementos minimalistas
- **Iconografía**: Líneas simples, trazos redondeados

## 📱 Uso en Android Studio

### 1. Logo Principal (fitmind_logo.xml)
```xml
<!-- Uso en SplashScreen -->
<Image
    painter = painterResource(id = R.drawable.fitmind_logo),
    contentDescription = "FitMind Logo",
    modifier = Modifier.size(120.dp)
)
```

### 2. Logo Splash (fitmind_splash.xml)
```xml
<!-- Uso en pantalla de carga -->
<Image
    painter = painterResource(id = R.drawable.fitmind_splash),
    contentDescription = "FitMind Splash",
    modifier = Modifier.size(120.dp)
)
```

### 3. Íconos de Navegación
```xml
<!-- Uso en BottomNavigationBar -->
Icon(
    imageVector = Icons.Filled.Home,
    contentDescription = "Inicio"
)
```

### 4. Ícono de la App (ic_launcher_foreground.svg)
- Se usa automáticamente como ícono de la aplicación
- Optimizado para diferentes densidades de pantalla
- Compatible con Android 12+ adaptive icons

## 🎯 Optimizaciones

### SVG vs PNG
- **SVG**: Escalable, editable, menor tamaño
- **PNG**: Mejor compatibilidad, más pesado
- **Recomendación**: Usar SVG para iconos, PNG para fotos

### Densidades de Pantalla
- **mdpi**: 1x (baseline)
- **hdpi**: 1.5x
- **xhdpi**: 2x
- **xxhdpi**: 3x
- **xxxhdpi**: 4x

### Optimización de Archivos
- Todos los SVG están optimizados para Android
- Colores definidos como variables CSS
- Gradientes optimizados para rendimiento
- Elementos agrupados lógicamente

## 🔧 Personalización

### Cambiar Colores
1. Edita los gradientes en cada archivo SVG
2. Actualiza las referencias de color en `Color.kt`
3. Recompila la aplicación

### Agregar Nuevos Íconos
1. Crea el archivo SVG siguiendo el estilo existente
2. Usa la misma paleta de colores
3. Mantén las dimensiones consistentes
4. Agrega al directorio `drawable/`

### Modificar el Logo
1. Edita `fitmind_logo.svg` para el logo principal
2. Edita `fitmind_splash.svg` para la versión splash
3. Actualiza `ic_launcher_foreground.svg` para el ícono de la app
4. Regenera el banner si es necesario

## 📐 Dimensiones Recomendadas

| Uso | Ancho | Alto | Formato |
|-----|-------|------|---------|
| Logo Principal | 512px | 512px | SVG |
| Logo Splash | 256px | 256px | SVG |
| Íconos de App | 108px | 108px | SVG |
| Íconos de Navegación | 128px | 128px | SVG |
| Banner README | 1000px | 300px | SVG |

## 🎨 Guía de Estilo

### Elementos de Diseño
- **Cerebro/mente**: Representa el aspecto mental
- **Ondas cerebrales**: Simbolizan actividad mental
- **Línea de frecuencia cardiaca**: Representa el aspecto físico
- **Elementos de fitness**: Dumbbells, formas geométricas

### Principios de Diseño
1. **Simplicidad**: Menos es más
2. **Consistencia**: Mismo estilo en todos los elementos
3. **Escalabilidad**: Funciona en diferentes tamaños
4. **Accesibilidad**: Contraste adecuado y legibilidad
5. **Modernidad**: Estética actual y profesional

## 🚀 Implementación

### En Jetpack Compose
```kotlin
// Usar íconos vectoriales
Icon(
    painter = painterResource(id = R.drawable.icon_home),
    contentDescription = "Inicio",
    modifier = Modifier.size(24.dp),
    tint = MaterialTheme.colorScheme.primary
)

// Usar imágenes
Image(
    painter = painterResource(id = R.drawable.fitmind_logo),
    contentDescription = "Logo",
    modifier = Modifier.size(120.dp)
)
```

### En XML (si es necesario)
```xml
<ImageView
    android:layout_width="120dp"
    android:layout_height="120dp"
    android:src="@drawable/fitmind_logo"
    android:contentDescription="FitMind Logo" />
```

## 📝 Notas de Desarrollo

- Todos los archivos SVG son editables con herramientas como Inkscape o Adobe Illustrator
- Los colores están definidos como gradientes para mejor flexibilidad
- Los elementos están agrupados lógicamente para facilitar la edición
- Se incluyen animaciones sutiles en algunos elementos (splash screen)

## 🔄 Actualizaciones Futuras

- [ ] Crear versiones PNG para mejor compatibilidad
- [ ] Agregar variantes de modo oscuro
- [ ] Crear íconos para diferentes estados (activo, inactivo, etc.)
- [ ] Desarrollar animaciones más complejas
- [ ] Crear variantes para diferentes idiomas
