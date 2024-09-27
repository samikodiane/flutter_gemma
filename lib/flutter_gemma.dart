import 'dart:async';
import 'package:flutter/services.dart';

export 'flutter_gemma_interface.dart';
export 'core/message.dart';

class FlutterGemma {
  static const MethodChannel _channel = MethodChannel('flutter_gemma');

  static Future<void> setModelOptions(String modelName, String modelDirectory) async {
    try {
      await _channel.invokeMethod('setModelOptions', {
        'modelName': modelName,
        'modelDirectory': modelDirectory,
      });
    } on PlatformException catch (e) {
      print("Error setting model options: '${e.message}'.");
      // Handle the error appropriately (e.g., show an error message to the user)
    }
  }
}
