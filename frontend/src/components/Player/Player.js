import React, {useEffect} from 'react';
import styles from './Player.module.css';
import {useDispatch, useSelector} from "react-redux";
import {deletePlayerData, loadPlayers} from "../../redux/actions/playerActions";
import {useNavigate} from "react-router-dom";
import {formatDate} from "../../util/DateUtil";

const Player = () => {
    const players = useSelector((state) => state.players);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        if (players.length === 0) {
            loadPlayers(dispatch);
        }
    }, [dispatch, players.length]);

    const navigateToDetails = (player) => navigate('/details', {replace: true, state: {player: player}});
    const deletePlayer = (player) => {
        const shouldDelete = window.confirm('Do you really want to delete this player?');
        if (shouldDelete) {
            deletePlayerData(dispatch, player);
        }
    }

    const createNewPlayer = () => {
        navigate('/details', {
            replace: true, state: {
                player: {
                    name: "",
                    rank: null,
                    birthdate: formatDate(new Date()),
                    birthplace: "",
                    turnedPro: formatDate(new Date()),
                    weight: null,
                    height: null,
                    coach: "",
                    stats: {
                        aces: null,
                        doubleFaults: null,
                        wins: null,
                        losses: null,
                    },
                    previousResults: [{points: null, opponentPoints: null, opponentName: ""}],
                    tournaments: [],
                    racquets: []
                }
            }
        });
    }

    return (<div className={styles.Player}>
        <div
            className="max-w-screen-xl mx-2 sm:mx-auto px-4 sm:px-6 lg:px-0 py-6 pb-20 sm:py-8 rounded-[2.25rem]
            sm:rounded-xl bg-white shadow-lg sm:shadow-md transform lg:-translate-y-12">
            <button onClick={createNewPlayer} title="Create player"
                    className="floating-button fixed z-100 right-8 bg-white-600 w-20 h-20 rounded-full drop-shadow-lg flex
                justify-center items-center text-white text-4xl  hover:drop-shadow-2xl bg-white
                hover:animate-bounce duration-300">
                <svg className="h-8 w-8 text-green-500" width="24" height="24"
                     viewBox="0 0 24 24" stroke="currentColor" fill="none">
                    <path stroke="none" d="M0 0h24v24H0z"/>
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
            </button>
            <div className="px-2 sm:px-6 py-2 align-middle inline-block min-w-full overflow-hidden">
                <table className="min-w-full animate-[wiggle_1s_ease-in-out_4s]">
                    <thead>
                    <tr>
                        <th className="text-left text-sm font-medium text-gray-500">Name</th>
                        <th className="text-left text-sm font-medium text-gray-500">Rank</th>
                        <th className="text-left text-sm font-medium text-gray-500">Birthdate</th>
                        <th className="text-left text-sm font-medium text-gray-500">Birthplace</th>
                        <th className="text-left text-sm font-medium text-gray-500">Turned Pro</th>
                        <th className="text-left text-sm font-medium text-gray-500">Weight</th>
                        <th className="text-left text-sm font-medium text-gray-500">Height</th>
                        <th className="text-left text-sm font-medium text-gray-500">Coach</th>
                        <th className="hidden sm:block text-left text-sm font-medium text-gray-500"></th>
                    </tr>
                    </thead>
                    <tbody>
                    {players.map(player => {
                        return (
                            <tr className="border-b border-gray-200" key={player.id}>
                                <td className="py-4 whitespace-nowrap">
                                    <div className="flex items-center space-x-2">
                                        <span>{player.name}</span></div>
                                </td>
                                <td className="py-4 whitespace-nowrap">
                                    <div className="flex items-center space-x-2">
                                        <svg className="h-4 w-4 text-blue-500" fill="none" viewBox="0 0 24 24"
                                             stroke="currentColor">
                                            <path
                                                d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"/>
                                        </svg>
                                        <span>{player.rank}</span></div>
                                </td>
                                <td className="py-4 whitespace-nowrap">
                                    <div className="flex items-center space-x-2">
                                        <svg className="h-4 w-4 text-blue-500" viewBox="0 0 24 24" fill="none"
                                             stroke="currentColor">
                                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                            <line x1="16" y1="2" x2="16" y2="6"/>
                                            <line x1="8" y1="2" x2="8" y2="6"/>
                                            <line x1="3" y1="10" x2="21" y2="10"/>
                                        </svg>
                                        <span>{player.birthdate}</span></div>
                                </td>
                                <td className="py-4 whitespace-nowrap">
                                    <div className="flex items-center space-x-2">
                                        <span>{player.birthplace}</span></div>
                                </td>
                                <td className="py-4 whitespace-nowrap">
                                    <div className="flex items-center space-x-2">
                                        <svg className="h-4 w-4 text-blue-500" viewBox="0 0 24 24" fill="none"
                                             stroke="currentColor">
                                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                            <line x1="16" y1="2" x2="16" y2="6"/>
                                            <line x1="8" y1="2" x2="8" y2="6"/>
                                            <line x1="3" y1="10" x2="21" y2="10"/>
                                        </svg>
                                        <span>{player.turnedPro}</span></div>
                                </td>
                                <td className="py-4 whitespace-nowrap">
                                    <div className="flex items-center space-x-2">
                                        <span>{player.weight} kg</span></div>
                                </td>
                                <td className="py-4 whitespace-nowrap">
                                    <div className="flex items-center space-x-2">
                                        <span>{player.height} cm</span></div>
                                </td>
                                <td className="py-4 whitespace-nowrap">
                                    <div className="flex items-center space-x-2">
                                        <span>{player.coach}</span></div>
                                </td>
                                <td className={styles.actionButtons}>
                                    <button onClick={() => deletePlayer(player)}>
                                        <svg className="h-8 w-8 text-red-500" viewBox="0 0 24 24" fill="none"
                                             stroke="currentColor">
                                            <polygon
                                                points="7.86 2 16.14 2 22 7.86 22 16.14 16.14 22 7.86 22 2 16.14 2 7.86 7.86 2"/>
                                            <line x1="15" y1="9" x2="9" y2="15"/>
                                            <line x1="9" y1="9" x2="15" y2="15"/>
                                        </svg>
                                    </button>
                                    <div></div>
                                    <button onClick={() => navigateToDetails(player)}>
                                        <svg className="h-8 w-8 text-teal-500" fill="none" viewBox="0 0 24 24"
                                             stroke="currentColor">
                                            <path
                                                d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/>
                                        </svg>
                                    </button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>

        </div>
    </div>);
};

Player.propTypes = {};

Player.defaultProps = {};

export default Player;
