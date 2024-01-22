import 'package:flutter_html_to_pdf/print_configuration_enums.dart';

class PrintPdfConfiguration {
  final String targetDirectory;
  final String targetName;
  final PrintSize printSize;
  final PrintOrientation printOrientation;

  /// `targetDirectory` is the desired path for the Pdf file.
  ///
  /// `targetName` is the name of the Pdf file
  ///
  /// `printSize` is the print size of the Pdf file
  ///
  /// `printOrientation` is the print orientation of the Pdf file
  PrintPdfConfiguration({
    required this.targetDirectory,
    required this.targetName,
    this.printSize = PrintSize.A4,
    this.printOrientation = PrintOrientation.Portrait,
  });

  /// Returns the final path for temporary Html File
  String get htmlFilePath => "$targetDirectory/$targetName.html";
}
