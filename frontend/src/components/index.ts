/**
 * Standard Imports of components
 * Non Async / Lazy loaded Components
 */
import Header from './header/Header';

/**
 * ComponentFactory is an object which exports functions that import components to be leverages by React Lazy & Suspense
 */
const ComponentFactory = {
    CardAsync: () => import(/* webpackChunkName: "Card" */ './card/Card'),
    TypeAsync: () => import(/* webpackChunkName: "Type" */ './type/Type'),
}

export {
    ComponentFactory,
    Header,
};
