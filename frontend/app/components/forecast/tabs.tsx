import { createContext, useContext, useState } from "react";
import Current from "./current";
import Daily from "./daily";
import Hourly from "./hourly";
import type { ForecastDetails } from "~/types/forecast";

type TabType = 'CURRENT' | 'HOURLY' | 'DAILY';

const tabContext = createContext({
    activeTabValue: 'CURRENT' as TabType,
    setActiveTabValue: (tab: TabType) => {}
});

function TabProvider({ children }: { children: React.ReactNode }) {
    const [activeTabValue, setActiveTabValue] = useState<TabType>('CURRENT');

    return (
        <tabContext.Provider value={{ activeTabValue, setActiveTabValue }}>
            {children}
        </tabContext.Provider>
    );
}

function TabTrigger({ value, children }: { value: TabType; children: React.ReactNode }) {
    const { activeTabValue, setActiveTabValue } = useContext(tabContext);

    const handleClick = () => {
        setActiveTabValue(value);
    };

    return (
        <button onClick={handleClick} className={`px-4 py-2 bg-gray-100 rounded-t-md border-t border-l border-r border-gray-300 ${activeTabValue === value ? "border-b border-b-white bg-white mb-[-1px]" : ""}`}>
            {children}
        </button>
    );
}

function TabContent({ value, children }: { value: TabType; children: React.ReactNode }) {
    const { activeTabValue } = useContext(tabContext);

    return activeTabValue === value ? <div className="pt-8 px-8">{children}</div> : null;
}

export default function Tabs({ forecast }: { forecast: ForecastDetails }) {
    return (
        <TabProvider>
            <div className="flex flex-col divide-y divide-gray-300">
                <div className="flex md:space-x-8 space-x-4 md:px-8 px-4">
                    <TabTrigger value="CURRENT">Current Conditions</TabTrigger>
                    <TabTrigger value="HOURLY">Hourly Forecast</TabTrigger>
                    <TabTrigger value="DAILY">Daily Forecast</TabTrigger>
                </div>
                <TabContent value="CURRENT">
                    <Current data={forecast.current} />
                </TabContent>
                <TabContent value="HOURLY">
                    <Hourly data={forecast.hourly} />
                </TabContent>
                <TabContent value="DAILY">
                    <Daily data={forecast.daily} />
                </TabContent>
            </div>
        </TabProvider>
    );
}