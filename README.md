
# react-native-amaptrace

## Getting started

`$ npm install react-native-amaptrace --save`

### Mostly automatic installation

`$ react-native link react-native-amaptrace`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-amaptrace` and add `RNAmaptrace.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNAmaptrace.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.aksaber.trace.RNAmaptracePackage;` to the imports at the top of the file
  - Add `new RNAmaptracePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-amaptrace'
  	project(':react-native-amaptrace').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-amaptrace/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-amaptrace')
  	```


## Usage
```javascript
import RNAmaptrace from 'react-native-amaptrace';

// TODO: What to do with the module?
RNAmaptrace;
```
  