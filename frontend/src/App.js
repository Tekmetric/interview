import * as React from "react";
import {Admin, Resource} from 'react-admin';
import dataProvider from './dataProvider';
import {InventoryCreate, InventoryEdit, InventoryList} from './inventories';


const App = () => (
    <Admin  dataProvider={dataProvider} >
        <Resource name="inventories" create={InventoryCreate} list={InventoryList} edit={InventoryEdit}/>
    </Admin>
);

export default App;
