


import ibrdtn from './ibrdtn';
import React, {Component} from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  TextInput,
  View,
  Button,
  DeviceEventEmitter,
} from 'react-native';

var EventEmitter = require('EventEmitter');
var Subscribable = require('Subscribable');

import Login from './Login'

const FBSDK = require('react-native-fbsdk');

const {
  LoginButton,
  AccessToken
} = FBSDK;

export default class SocialDisasters extends Component {
  
  messageRecv (e: Event) {
    console.log("js, newMessage");
    this.setState({messageRecv: e.MESSAGE})
  }
  constructor(props) {
    super(props);
    this.state = { text: 'Password' ,
                   dtnDir: 'dtn://android-9b2cc423.dtn/socialdisasters',
                   message: 'message',
                   messageRecv: '',
                };
    ibrdtn.init(); 
    DeviceEventEmitter.addListener('newMessage', this.messageRecv.bind(this));
    //var ScrollResponderMixin = {
        //mixins: [Subscribable.Mixin],

        //componentWillMount: function() {
            //DeviceEventEmitter.addListener('newMessage', function(e: Event) {
                //console.log("js, newMessage");
                //console.log(e)
                //this.setState({messageRecv: "hola"})
            //});
        //}
    //}
  }


  render() {
    return (
      <View>
        <Text>Welcome to the Social Disaster app!</Text>
        <TextInput
        style={{height: 40, borderColor: 'gray', borderWidth: 1}}
        onChangeText={(text) => this.setState({text})}
        value={this.state.text}
        />
        <LoginButton
          publishPermissions={["publish_actions"]}
          onLoginFinished={
            (error, result) => {
              if (error) {
                alert("Login failed with error: " + result.error);
              } else if (result.isCancelled) {
                alert("Login was cancelled");
              } else {
                // obtain access token
                AccessToken.getCurrentAccessToken().then(
                  (data) => {
                    var userToken=data.accessToken.toString();
                    var userId =data.getUserId().toString(); 
                    alert(userToken)
                    //alert(userId)
                  }
                )
              }
            }
          }
          onLogoutFinished={() => alert("User logged out")}/>

        <Text style={styles.textoDTN}>Dirección DTN:</Text>
        <TextInput
            style={styles.inputDirDTN}
            onChangeText={(dtnDir) => this.setState({dtnDir})}
            value={this.state.dtnDir}
        />
        <Text style={styles.labelMessage}>Mensaje</Text>
        <TextInput
            style={styles.inputMessage}
            onChangeText={(message) => this.setState({message})}
            value={this.state.message}
        />
        <Button
            style={styles.buttonSend}
            onPress={this.onButtonPress.bind(this)} title="Send" color="#841584"
        />
        <Text style={styles.labelMessage}>
        {"Mensaje recivido: "} {this.state.messageRecv}
        </Text>
      </View>
    );
  }
    onButtonPress() {
        console.log("boton send");
        let dirDtn = this.state.dtnDir;
        let message = this.state.message;
        ibrdtn.send(dirDtn, message);
    }
}

const styles = StyleSheet.create({
    inputDirDTN: {
        borderColor: 'gray',
        height: 50,
    },
    buttonSend:{
        color: 'red'
    },
    textoDTN: {
        color: 'blue',
        height: 50,
        marginTop: 10,
        marginBottom: 10,
    },
    labelMessage: {
        marginTop: 10,
        marginBottom: 10,
    },
    inputMessage: {
        marginTop: 10,
        marginBottom: 10,
    }
})





AppRegistry.registerComponent('SocialDisasters', () => SocialDisasters);
