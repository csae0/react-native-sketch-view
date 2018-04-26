
package com.reactlibrary;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.sketchView.SketchFile;
import com.sketchView.SketchViewContainer;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class RNSketchViewManager extends SimpleViewManager<SketchViewContainer> {

  private static final String RN_PACKAGE = "RNSketchView";

  private static final String PROPS_SELECTED_TOOL = "selectedTool";
  private static final String PROPS_TOOL_COLOR = "toolColor";
  private static final String PROPS_TOOL_THICKNESS = "toolThickness";
  private static final String PROPS_LOCAL_SOURCE_IMAGE_PATH  = "localSourceImagePath";

  private static final int COMMAND_CLEAR_SKETCH = 321;
  private static final int COMMAND_SAVE_SKETCH = 780;
  private static final int COMMAND_EXPORT_SKETCH = 511;
  private static final int COMMAND_CHANGE_TOOL = 406;
  private static final int COMMAND_SET_EDITED = 123;
  @Override
  public String getName() {
    return RN_PACKAGE;
  }

  @Override
  protected SketchViewContainer createViewInstance(ThemedReactContext reactContext) {
    return new SketchViewContainer(reactContext);
  }

  @ReactProp(name = PROPS_SELECTED_TOOL)
  public void setSelectedTool(SketchViewContainer viewContainer, @NonNull Integer toolId) {
    viewContainer.sketchView.setToolType(toolId);
  }

  @ReactProp(name = PROPS_TOOL_COLOR, defaultInt = Color.BLACK, customType = "Color")
  public void setToolColor(SketchViewContainer viewContainer, @NonNull Integer color) {
    viewContainer.sketchView.setToolColor(color);
  }

  @ReactProp(name = PROPS_TOOL_THICKNESS)
  public void setPropsToolThickness(SketchViewContainer viewContainer, @NonNull float thickness) {
    viewContainer.sketchView.setToolThickness(thickness);
  }

  @ReactProp(name = PROPS_LOCAL_SOURCE_IMAGE_PATH)
  public void setLocalSourceImagePath(SketchViewContainer viewContainer, @NonNull String localSourceImagePath) {
    viewContainer.openSketchFile(localSourceImagePath);
  }

  @Nullable
  @Override
  public Map<String, Integer> getCommandsMap() {
    return MapBuilder.of(
            "clearSketch",
            COMMAND_CLEAR_SKETCH,
            "saveSketch",
            COMMAND_SAVE_SKETCH,
            "exportSketch",
            COMMAND_EXPORT_SKETCH,
            "changeTool",
            COMMAND_CHANGE_TOOL,
            "setEdited",
            COMMAND_SET_EDITED);
  }

  @Override
  public void receiveCommand(SketchViewContainer root, int commandId, @Nullable ReadableArray args) {
    Assertions.assertNotNull(root);

    switch (commandId) {
      case COMMAND_CLEAR_SKETCH:
        root.sketchView.clear();
        return;
      case COMMAND_CHANGE_TOOL:
        Assertions.assertNotNull(args);
        int toolId = args.getInt(0);
        root.sketchView.setToolType(toolId);
        return;
      case COMMAND_SAVE_SKETCH:
        try {
          SketchFile sketchFile = root.saveToLocalCache();
          onSaveSketch(root, sketchFile);
          return;
        } catch (IOException e) {
          e.printStackTrace();
        }
      case COMMAND_EXPORT_SKETCH:
        String encoding = root.getBase64();
        onExportSketch(root, encoding);
        return;
      case COMMAND_SET_EDITED:
        Assertions.assertNotNull(args);
        root.sketchView.setSketchViewEdited(args.getBoolean(0));
        return;
      default:
        throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Unsupported command %d.", commandId));
    }
  }

  private void onSaveSketch(SketchViewContainer root, SketchFile sketchFile) {
    WritableMap event = Arguments.createMap();
    event.putString("localFilePath", sketchFile.localFilePath);
    event.putInt("imageWidth", sketchFile.width);
    event.putInt("imageHeight", sketchFile.height);
    sendEvent(root, "onSaveSketch", event);
  }

  private void onExportSketch(SketchViewContainer root, String encoding) {
    WritableMap event = Arguments.createMap();
    event.putString("base64Encoded", encoding);
    sendEvent(root, "onExportSketch", event);
  }

  private void sendEvent(View view, String eventType, WritableMap event) {
    WritableMap nativeEvent = Arguments.createMap();
    nativeEvent.putString("type", eventType);
    nativeEvent.putMap("event", event);
    ReactContext reactContext = (ReactContext) view.getContext();
    reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(view.getId(), "topChange", nativeEvent);
  }



}