import React from 'react';
import {
  AppRegistry,
  Platform,
  StyleSheet,
  Text,
  View,
  ListView,
  TouchableHighlight,
  Button
} from 'react-native';

import { StackNavigator } from 'react-navigation';

import ViewChat from './ViewChat';

import {GiftedChat, Actions, Bubble} from 'react-native-gifted-chat';
import CustomActions from './CustomActions';
import CustomView from './CustomView';
import Searcher from './Searcher';

const styles = StyleSheet.create({

  container:{
  	flex: 1,
    backgroundColor: '#f0f0f0',
    alignItems: 'stretch',
  },  
  viewContainer:{
    flex: 5,
    flexDirection: 'column',
    alignItems: 'stretch',    
    backgroundColor: 'powderblue',
  },
  containerListContact: {
    flex: 1,
    marginTop: 20,
  },
  containerContact: {
    flex: 1,
    padding: 12,
    flexDirection: 'row',
    alignItems: 'center',    
  },
  avatarContact: {
    height: 40,
    width: 40,
    borderRadius: 20,
    backgroundColor: 'steelblue',
    alignContent: 'center',
  },
  textCircle:{
  	textAlign: 'center',
  	fontSize: 24,
  	color: 'white',
  },
  textContact: {
    marginLeft: 12,
    fontSize: 16,
  },
  separator: {
    flex: 1,
    height: StyleSheet.hairlineWidth,
    backgroundColor: '#8E8E8E',
  },
});

export default class Contacts extends React.Component {

	static navigationOptions = {
	    title: 'Chat for Disaster',
  	};

	constructor(props) {
    super(props);
    const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
    this.state = {
      dataSource: ds.cloneWithRows([
        'John', 'Joel', 'James', 'Jimmy', 'Jackson', 'Jillian', 'Julie', 'Devin'
      ])
    };
  }
  render(){

  	const { navigate } = this.props.navigation;
  	
  	return (
 	 
	  <View style={styles.container}>
		
	    <View style={styles.viewContainer}>
	      <ListView
	        style={styles.containerListContact}
	        dataSource={this.state.dataSource}
	        renderRow={(data) => 				
        		<TouchableHighlight onPress={ () => navigate('Chat', { user: {data}.data } )} >
					<View style={styles.containerContact} >
						<View style={styles.avatarContact}>
							<Text style={styles.textCircle}> {{data}.data.substr(0,2)} </Text>
						</View>
						<Text style={styles.textContact}>
							{{data}.data}
						</Text>
					</View>
				</TouchableHighlight>
	        }
	        renderSeparator={(sectionId, rowId) => <View key={rowId} style={styles.separator} />}
	        renderHeader={()=> <Searcher />}
	      />
        </View>

    </View>

    );
  }  	
}