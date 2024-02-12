import React, { createContext, useCallback, useContext, useState } from "react"

const initialState = {
    persons: {},
    list: () => undefined,
    update: () => undefined,
    get: () => undefined,
    remove: () => undefined,
    create: () => undefined,
}
const PersonContext = createContext(initialState)
export const usePersons = () => useContext(PersonContext)

const ProvidePersons = ({ children }) => {
    const [persons, setPersons] = useState({})

    const list = useCallback(() => {
        fetch('/api/persons')
            .then(response => response.json())
            .then(response => response.reduce((acc, value) => ({ ...acc, [value.id]: value }), {}))
            .then(setPersons)
    }, [setPersons])

    const get = useCallback((id) => {
        fetch(`/api/persons/${id}`)
            .then(response => response.json())
            .then(response => setPersons(persons => ({ ...persons, [response.id]: response })))
    }, [setPersons])

    const update = useCallback((id, person) => {
        fetch(`/api/persons/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(person)
        })
            .then(response => response.json())
            .then(response => setPersons(persons => ({ ...persons, [response.id]: response })))
    }, [setPersons])

    const create = useCallback((person) => {
        fetch(`/api/persons`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(person)
        })
            .then(response => response.json())
            .then(response => setPersons(persons => ({ ...persons, [response.id]: response })))
    }, [setPersons])

    const remove = useCallback((id) => {
        fetch(`/api/persons/${id}`, { method: 'DELETE' })
            .then(() => {
                setPersons(persons => {
                    return Object.entries(persons).reduce((acc, [key, value]) => {
                        return key === id ? acc : { ...acc, [key]: value }
                    }, {})
                })
            })
    }, [setPersons]);

    return (
        <PersonContext.Provider value={{ persons, list, get, update, remove, create }}>
            {children}
        </PersonContext.Provider>
    )
}

export default ProvidePersons