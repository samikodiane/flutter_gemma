package dev.flutterberlin.flutter_gemma

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class InferenceModel private constructor(context: Context, val modelPath: String, maxTokens: Int, temperature: Float, randomSeed: Int, topK: Int) {
    private var llmInference: LlmInference

    private val _partialResults = MutableSharedFlow<Pair<String, Boolean>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val partialResults: SharedFlow<Pair<String, Boolean>> = _partialResults.asSharedFlow()

    init { 
        // Check if the model exists at the given path
        if (!File(modelPath).exists()) {
            throw IllegalArgumentException("Model not found at path: $modelPath")
        }

        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(modelPath)
            .setMaxTokens(maxTokens)
            .setTemperature(temperature)
            .setRandomSeed(randomSeed)
            .setTopK(topK)
            .setResultListener { partialResult, done ->
                _partialResults.tryEmit(partialResult to done)
            }
            .build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    // ... (generateResponse and generateResponseAsync methods remain the same) 

    companion object {
        private var instance: InferenceModel? = null

        fun getInstance(context: Context, modelName: String, modelDirectory: String, maxTokens: Int, temperature: Float, randomSeed: Int, topK: Int): InferenceModel {
            val modelPath = "$modelDirectory/$modelName"

            return if (instance != null && instance!!.modelPath == modelPath) {
                instance!!
            } else {
                instance = InferenceModel(context, modelPath, maxTokens, temperature, randomSeed, topK)
                instance!!
            }
        }
    }
} 
