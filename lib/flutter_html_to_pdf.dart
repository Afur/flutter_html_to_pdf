import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:flutter_html_to_pdf/file_utils.dart';
import 'package:flutter_html_to_pdf/pdf_print_configuration.dart';
import 'package:flutter_html_to_pdf/print_configuration_enums.dart';

export 'package:flutter_html_to_pdf/print_configuration_enums.dart';
export 'package:flutter_html_to_pdf/pdf_print_configuration.dart';

/// HTML to PDF Converter
class FlutterHtmlToPdf {
  static const MethodChannel _channel =
      const MethodChannel('flutter_html_to_pdf');

  /// Creates PDF Document from HTML content
  /// Can throw a [PlatformException] or (unlikely) a [MissingPluginException] converting html to pdf
  static Future<File> convertFromHtmlContent({
    required String htmlContent,
    required PrintPdfConfiguration printPdfConfiguration,
  }) async {
    final File temporaryCreatedHtmlFile =
        await FileUtils.createFileWithStringContent(
      htmlContent,
      printPdfConfiguration.htmlFilePath,
    );
    await FileUtils.appendStyleTagToHtmlFile(temporaryCreatedHtmlFile.path);

    final String generatedPdfFilePath = await _convertFromHtmlFilePath(
      temporaryCreatedHtmlFile.path,
      printPdfConfiguration.printSize,
      printPdfConfiguration.printOrientation,
    );

    temporaryCreatedHtmlFile.delete();

    return FileUtils.copyAndDeleteOriginalFile(
      generatedPdfFilePath,
      printPdfConfiguration.targetDirectory,
      printPdfConfiguration.targetName,
    );
  }

  /// Creates PDF Document from File that contains HTML content
  /// Can throw a [PlatformException] or (unlikely) a [MissingPluginException] converting html to pdf
  static Future<File> convertFromHtmlFile({
    required File htmlFile,
    required PrintPdfConfiguration printPdfConfiguration,
  }) async {
    await FileUtils.appendStyleTagToHtmlFile(htmlFile.path);
    final String generatedPdfFilePath = await _convertFromHtmlFilePath(
      htmlFile.path,
      printPdfConfiguration.printSize,
      printPdfConfiguration.printOrientation,
    );

    return FileUtils.copyAndDeleteOriginalFile(
      generatedPdfFilePath,
      printPdfConfiguration.targetDirectory,
      printPdfConfiguration.targetName,
    );
  }

  /// Creates PDF Document from path to File that contains HTML content
  /// Can throw a [PlatformException] or (unlikely) a [MissingPluginException] converting html to pdf
  static Future<File> convertFromHtmlFilePath({
    required String htmlFilePath,
    required PrintPdfConfiguration printPdfConfiguration,
  }) async {
    await FileUtils.appendStyleTagToHtmlFile(htmlFilePath);
    final String generatedPdfFilePath = await _convertFromHtmlFilePath(
      htmlFilePath,
      printPdfConfiguration.printSize,
      printPdfConfiguration.printOrientation,
    );

    return FileUtils.copyAndDeleteOriginalFile(
      generatedPdfFilePath,
      printPdfConfiguration.targetDirectory,
      printPdfConfiguration.targetName,
    );
  }

  /// Assumes the invokeMethod call will return successfully
  static Future<String> _convertFromHtmlFilePath(
    String htmlFilePath,
    PrintSize printSize,
    PrintOrientation printOrientation,
  ) async {
    int width = printSize
        .getDimensionsInPixels[printOrientation.getWidthDimensionIndex];
    int height = printSize
        .getDimensionsInPixels[printOrientation.getHeightDimensionIndex];

    return await _channel.invokeMethod(
      'convertHtmlToPdf',
      <String, dynamic>{
        'htmlFilePath': htmlFilePath,
        'width': width,
        'height': height,
        'printSize': printSize.printSizeKey,
        'orientation': printOrientation.orientationKey,
      },
    ) as String;
  }
}
