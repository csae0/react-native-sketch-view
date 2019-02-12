//
//  SketchViewContainer.m
//  Sketch
//
//  Created by Keshav on 06/04/17.
//  Copyright © 2017 Particle41. All rights reserved.
//

#import "SketchViewContainer.h"

@implementation SketchViewContainer

-(BOOL)openSketchFile:(NSString *)localFilePath
{
    UIImage *image = [UIImage imageWithContentsOfFile:localFilePath];
    if(image) {
        [self.sketchView setViewImage:image];
        return YES;
    }
    return NO;
}

-(SketchFile *)saveToLocalCache: (NSString *)saveLocation
{
    UIImage *image = [SketchViewContainer imageWithView:self];
    
    NSString * saveDirectory = NSTemporaryDirectory();
    if ([saveLocation length]) {
        saveDirectory = saveLocation;
    }
    
    NSURL *saveDir = [NSURL fileURLWithPath:saveDirectory isDirectory:YES];
    NSString *fileName = [NSString stringWithFormat:@"sketch_%@.png", [[NSUUID UUID] UUIDString]];
    NSURL *fileURL = [saveDir URLByAppendingPathComponent:fileName];
    
    NSData *imageData = UIImagePNGRepresentation(image);
    [imageData writeToURL:fileURL atomically:YES];
    
    SketchFile *sketchFile = [[SketchFile alloc] init];
    sketchFile.localFilePath = [fileURL path];
    sketchFile.size = [image size];
    return sketchFile;
}

-(NSString *)getBase64
{
    UIImage *image = [SketchViewContainer imageWithView:self];
    NSData *imageData = UIImagePNGRepresentation(image);
    return [imageData base64EncodedStringWithOptions:0];
}

+ (UIImage *) imageWithView:(UIView *)view
{
    UIGraphicsBeginImageContextWithOptions(view.bounds.size, false, [[UIScreen mainScreen] scale]);
    [view.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage * img = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return img;
}

@end
