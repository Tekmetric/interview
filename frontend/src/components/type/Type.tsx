import React from 'react';
import {classNames} from "../../utils/Utils";
import styles from './Type.module.scss';
export interface TypeProp {
    /**
     * Name of the Type to render inside the pill
     */
    name: string;
};

/**
 * Type component is a visual "Pill" to render the type(s) of the Pokemon
 * @param props
 * @constructor
 */
const Type = (props: TypeProp) => {
    const {name} = props;

    return (
        <span className={classNames(styles.pill, styles[name])}>
            {name}
        </span>
    )
};

export default Type;
