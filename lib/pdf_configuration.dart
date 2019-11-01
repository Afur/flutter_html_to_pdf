class PdfConfiguration {
  double width;
  double height;
  int additionalConvertDelay;

  PdfConfiguration({double width, double height, int additionalConvertDelay}) {
    this.width = width;
    this.height = height;
    this.additionalConvertDelay = additionalConvertDelay;
  }
}