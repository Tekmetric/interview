import React from 'react';
import {classNames} from "../../utils/Utils";
import styles from './Type.module.scss';
export interface TypeProp {
    name: string;
};

const Type = (props: TypeProp) => {
    const {name} = props;

    return (
        <span className={classNames(styles.pill, styles[name])}>
            {name}
        </span>
    )
};

export default Type;
