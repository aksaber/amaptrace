
import { NativeModules, NativeEventEmitter } from 'react-native';

const { RNAmaptrace } = NativeModules;

export const AmapTraceEventEmitter = new NativeEventEmitter(NativeModules.AmapTrace);

export default RNAmaptrace;
