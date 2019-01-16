#import "FlutterHtmlToPdfPlugin.h"
#import <flutter_html_to_pdf/flutter_html_to_pdf-Swift.h>

@implementation FlutterHtmlToPdfPlugin
UIViewController *_viewController;

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterHtmlToPdfPlugin registerWithRegistrar:registrar];
}@end
