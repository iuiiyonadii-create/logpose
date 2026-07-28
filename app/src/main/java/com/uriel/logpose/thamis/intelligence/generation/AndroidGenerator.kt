package com.uriel.logpose.thamis.intelligence.generation

/**
 * FASE FINAL — CODE GENERATION
 * Generador especializado en componentes Android (Compose, ViewModel).
 */
object AndroidGenerator {

    fun generateViewModel(featureName: String): String {
        return """
            @HiltViewModel
            class ${featureName}ViewModel @Inject constructor(
                private val repository: ${featureName}Repository
            ) : ViewModel() {
                private val _state = MutableStateFlow(null)
                val state: StateFlow<Any?> = _state
            }
        """.trimIndent()
    }

    fun generateComposeScreen(name: String): String {
        return """
            @Composable
            fun ${name}Screen(viewModel: ${name}ViewModel) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = "$name Feature")
                }
            }
        """.trimIndent()
    }
}
