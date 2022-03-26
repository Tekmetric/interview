import * as React from "react";
import {
    Create, List, Edit, SimpleForm, Datagrid, TextField, EmailField, BooleanField,
    ChipField, NumberField, TextInput, BooleanInput, RadioButtonGroupInput, required, email,
} from 'react-admin';

export const InventoryCreate = props => (
    <Create {...props}>
        <SimpleForm redirect="list">
            <TextInput source="partName" validate={required()}/>
            <TextInput source="partNumber" validate={required()}/>
            <TextInput source="brand" validate={required()}/>
            <TextInput source="supportEmail" type="email" validate={[required(),email()]} />
            <TextInput source="quantity" type="number" validate={required()}/>
            <RadioButtonGroupInput source="type" validate={required()} choices={[
                {id: 'TYPE_A', name: 'TYPE A'},
                {id: 'TYPE_B', name: 'TYPE B'},
                {id: 'TYPE_C', name: 'TYPE C'},
            ]}/>
            <BooleanInput source="status" validate={required()} label="Available"/>
        </SimpleForm>
    </Create>
);

export const InventoryEdit = props => (
    <Edit mutationMode="pessimistic" {...props}>
        <SimpleForm>
            <TextInput source="id" disabled/>
            <TextInput source="partName" validate={required()}/>
            <TextInput source="partNumber" validate={required()}/>
            <TextInput source="brand" validate={required()}/>
            <TextInput source="supportEmail" type="email" validate={required()}/>
            <TextInput source="quantity" type="number" validate={required()}/>
            <RadioButtonGroupInput source="type" validate={required()} choices={[
                {id: 'TYPE_A', name: 'TYPE A'},
                {id: 'TYPE_B', name: 'TYPE B'},
                {id: 'TYPE_C', name: 'TYPE C'},
            ]}/>
            <BooleanInput source="status" validate={required()} label="Available"/>
        </SimpleForm>
    </Edit>
);

const InventoryFilters = [
    <TextInput source="q" label="Search" alwaysOn />
];

export const InventoryList = props => {
    return <List {...props} sort={{ field: 'id', order: 'DESC' }} filters={InventoryFilters}>
        <Datagrid rowClick="edit">
            <TextField source="id"/>
            <TextField source="partName" label="Name"/>
            <TextField source="partNumber" label="Number"/>
            <TextField source="brand"/>
            <EmailField source="supportEmail" label="Email"/>
            <NumberField source="quantity"/>
            <ChipField source="type"/>
            <BooleanField source="status" valueLabelTrue="Available" valueLabelFalse="Not Available"/>
        </Datagrid>
    </List>
};


