import React, {useEffect} from 'react';
import {useFieldArray, useForm, Controller} from 'react-hook-form';
import Select from "react-select";
import {useDispatch, useSelector} from "react-redux";
import {loadTournaments} from "../../../redux/actions/tournamentActions";
import {loadRacquets} from "../../../redux/actions/racquetActions";
import {useLocation, useNavigate} from "react-router-dom";
import moment from 'moment';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import {PlayerAPI} from "../../../api/config/PlayerAPI";
import {createPlayersSuccess, updatePlayersSuccess} from "../../../redux/actions/playerActions";
import {formatDate} from "../../../util/DateUtil";

const Details = () => {
    const {state} = useLocation();
    const tournaments = useSelector((state) => state.tournaments);
    const racquets = useSelector((state) => state.racquets);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        if (!tournaments || tournaments.length === 0) {
            loadTournaments(dispatch);
        }
        if (!racquets || racquets.length === 0) {
            loadRacquets(dispatch);
        }
    }, [dispatch, racquets, tournaments]);

    const defaultFormValues = state.player;
    const {
        getValues,
        control,
        register,
        handleSubmit,
        setValue,
        formState: {errors}
    } = useForm({
        defaultValues: defaultFormValues
    });
    const {
        fields: previousResultsFields,
        append: appendPreviousResults,
        remove: removePreviousResult
    } = useFieldArray({
        control,
        name: "previousResults"
    });
    const {
        fields: tournamentFields,
        remove: removeTournament,
        append: appendTournament,
    } = useFieldArray({
        control,
        name: "tournaments"
    });
    let {
        fields: racquetsFields,
        remove: removeRacquets,
        append: appendRacquets,
    } = useFieldArray({
        control,
        name: "racquets"
    });

    const onSubmit = (player) => {
        return PlayerAPI
            .save(player)
            .then(savedPlayer => {
                player.id
                    ? dispatch(updatePlayersSuccess(savedPlayer))
                    : dispatch(createPlayersSuccess(savedPlayer));
                navigate('/', {replace: true, state: {}})
            })
            .catch(error => {
                throw error;
            });
    };

    const handleTurnedProDate = (dateChange) => {
        setValue("turnedPro", formatDate(dateChange), {
            shouldDirty: true
        });
    };

    const handleBirthdate = (dateChange) => {
        setValue("birthdate", formatDate(dateChange), {
            shouldDirty: true
        });
    };

    const cancel = () => {
        navigate('/', {replace: true, state: {}});
    }

    return (<form onSubmit={handleSubmit(onSubmit)}>
            <div
                className="max-w-screen-xl mx-2 sm:mx-auto px-4 sm:px-6 lg:px-0 py-6 pb-20 sm:py-8 rounded-[2.25rem]
                sm:rounded-xl bg-white shadow-lg sm:shadow-md transform lg:-translate-y-12">
                <div className="px-2 sm:px-6 py-2 align-middle inline-block overflow-hidden">
                    <div className="mt-16 grid gap-4 gap-y-2 text-sm grid-cols-1 lg:grid-cols-3">
                        <div className="text-gray-600">
                            <p className="font-medium text-lg">Personal Informations</p>
                        </div>
                        <div className="lg:col-span-2">
                            <div className="grid gap-4 gap-y-2 text-sm grid-cols-1 md:grid-cols-5">
                                <div className="md:col-span-4">
                                    <label htmlFor="full_name">Full Name</label>
                                    <input type="text" name="full_name" id="full_name"
                                           className={errors.name?.type === 'required' ? "invalid h-10 border mt-1 rounded px-4 w-full bg-gray-50" : "h-10 border mt-1 rounded px-4 w-full bg-gray-50"}
                                           {...register("name", {
                                               required: {
                                                   value: true
                                               },
                                           })} />
                                </div>

                                <div className="md:col-span-1">
                                    <label htmlFor="rank">Rank</label>
                                    <input type="number" name="rank" id="rank"
                                           className={errors.name?.type === 'required' ? "invalid h-10 border mt-1 rounded px-4 w-full bg-gray-50" : "h-10 border mt-1 rounded px-4 w-full bg-gray-50"}
                                           {...register("rank", {
                                               required: {
                                                   value: true
                                               },
                                           })} />
                                </div>
                                <div className="md:col-span-2">
                                    <label htmlFor="birthplace">Birthdate</label>
                                    <br/>
                                    <Controller
                                        name="birthplace"
                                        control={control}
                                        defaultValue={moment(getValues().birthdate, "DD-MM-yyyy").toDate()}
                                        render={() => (
                                            <DatePicker
                                                className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                                selected={moment(getValues().birthdate, "DD-MM-yyyy").toDate()}
                                                placeholderText="Select date"
                                                dateFormat="dd-MM-yyyy"
                                                onChange={handleBirthdate}
                                            />
                                        )}
                                    ></Controller>
                                </div>
                                <div className="md:col-span-1">
                                    <label htmlFor="birthplace">Birthplace</label>
                                    <input type="text" name="birthplace" id="birthplace"
                                           className={errors.name?.type === 'required' ? "invalid h-10 border mt-1 rounded px-4 w-full bg-gray-50" : "h-10 border mt-1 rounded px-4 w-full bg-gray-50"}
                                           {...register("birthplace", {
                                               required: {
                                                   value: true
                                               },
                                           })}
                                           placeholder=""/>
                                </div>
                                <div className="md:col-span-2">
                                    <label htmlFor="turnedPro">Turned Pro</label>
                                    <br/>
                                    <Controller
                                        name="turnedPro"
                                        control={control}
                                        defaultValue={moment(getValues().turnedPro, "DD-MM-yyyy").toDate()}
                                        render={() => (
                                            <DatePicker
                                                className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                                selected={moment(getValues().turnedPro, "DD-MM-yyyy").toDate()}
                                                placeholderText="Select date"
                                                dateFormat={"dd-MM-yyyy"}
                                                onChange={handleTurnedProDate}
                                            />
                                        )}
                                    />
                                </div>

                                <div className="md:col-span-1">
                                    <label htmlFor="weight">Weight</label>
                                    <input type="number" name="weight" id="weight"
                                           className={errors.name?.type === 'required' ? "invalid transition-all flex items-center h-10 border mt-1 rounded px-4\n" +
                                               "w-full bg-gray-50" : "transition-all flex items-center h-10 border mt-1 rounded px-4\n" +
                                               "w-full bg-gray-50"}
                                           {...register("weight", {
                                               valueAsNumber: true,
                                               required: {
                                                   value: true,
                                                   message: 'Name is required',
                                               },
                                           })} />
                                </div>
                                <div className="md:col-span-1">
                                    <label htmlFor="height">Height</label>
                                    <input type="number" name="height" id="height"
                                           className={errors.name?.type === 'required' ? "invalid transition-all flex items-center h-10 border mt-1 rounded px-4\n" +
                                               "w-full bg-gray-50" : "transition-all flex items-center h-10 border mt-1 rounded px-4\n" +
                                               "w-full bg-gray-50"}                                           {...register("height", {
                                               valueAsNumber: true,
                                               required: {
                                                   value: true
                                               },
                                           })} />
                                </div>
                                <div className="md:col-span-3">
                                    <label htmlFor="coach">Coach</label>
                                    <input type="text" name="coach" id="coach"
                                           className="transition-all flex items-center h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                           {...register("coach")} />
                                </div>


                            </div>
                        </div>
                    </div>
                    <hr className="mt-8"/>
                    <div className="grid gap-4 gap-y-2 text-sm grid-cols-1 lg:grid-cols-3 mt-4">
                        <div className="text-gray-600">
                            <p className="font-medium text-lg">Statistics</p>
                        </div>
                        <div className="lg:col-span-2">
                            <div className="grid gap-4 gap-y-2 text-sm grid-cols-1 md:grid-cols-5">
                                <div className="md:col-span-1">
                                    <label htmlFor="aces">Aces</label>
                                    <input type="number" name="aces" id="aces"
                                           className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                           {...register("stats.aces", {valueAsNumber: true})}/>
                                </div>

                                <div className="md:col-span-1">
                                    <label htmlFor="doubleFaults">Double faults</label>
                                    <input type="number" name="doubleFaults" id="doubleFaults"
                                           className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                           {...register("stats.doubleFaults", {valueAsNumber: true})}/>
                                </div>

                                <div className="md:col-span-1">
                                    <label htmlFor="wins">Wins</label>
                                    <input type="number" name="wins" id="wins"
                                           className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                           {...register("stats.wins", {valueAsNumber: true})}/>
                                </div>
                                <div className="md:col-span-1">
                                    <label htmlFor="losses">Losses</label>
                                    <input type="number" name="losses" id="losses"
                                           className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                           {...register("stats.losses", {valueAsNumber: true})}/>
                                </div>
                                <div className="md:col-span-1">
                                    <label htmlFor="tournamentsPlayed">Tournaments Played</label>
                                    <input type="number" name="tournamentsPlayed" id="tournamentsPlayed"
                                           className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                           {...register("stats.tournamentsPlayed", {valueAsNumber: true})}/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <hr className="mt-8"/>
                    <div className="grid gap-4 gap-y-2 text-sm grid-cols-1 lg:grid-cols-3 mt-4">
                        <div className="text-gray-600">
                            <p className="font-medium text-lg">Tournaments & Racquets</p>
                        </div>
                        <div className="lg:col-span-2">
                            <div className="grid gap-4 gap-y-2 text-sm grid-cols-1 md:grid-cols-5">
                                <div className="md:col-span-2">
                                    <Select options={tournaments} isMulti
                                            defaultValue={tournamentFields}
                                            name="tournaments"
                                            getOptionLabel={(option) => option.name}
                                            getOptionValue={(option) => option.id}
                                            className="basic-multi-select"
                                            onChange={(selectedOption) => {
                                                removeTournament(selectedOption)
                                                appendTournament(selectedOption);
                                            }}
                                            classNamePrefix="select"/>
                                </div>

                                <div className="md:col-span-3">
                                    <Select options={racquets} isMulti
                                            defaultValue={racquetsFields}
                                            name="racquets"
                                            getOptionLabel={(option) => option.brand + " - " + option.model}
                                            getOptionValue={(option) => option.id}
                                            onChange={(selectedOption) => {
                                                removeRacquets(selectedOption);
                                                appendRacquets(selectedOption);
                                            }}
                                            className="basic-multi-select"
                                            classNamePrefix="select"/>
                                </div>
                            </div>
                        </div>
                    </div>

                    <hr className="mt-8"/>
                    {previousResultsFields.map((field, index) => (
                        <div key={index} className="grid gap-4 gap-y-2 text-sm grid-cols-1 lg:grid-cols-3 mt-4">
                            <div className="text-gray-600">
                                {index === 0 && <p className="font-medium text-lg">Previous Results</p>}
                            </div>
                            <div className="lg:col-span-2 text-gray-600">
                                <div className="grid gap-4 gap-y-2 text-sm grid-cols-1 md:grid-cols-5">
                                    <div className="md:col-span-2">
                                        <label htmlFor="opponentName">Opponent Name</label>
                                        <input type="text" name="opponentName" id="opponentName"
                                               className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                               {...register(`previousResults.${index}.opponentName`)}/>
                                    </div>

                                    <div className="md:col-span-1">
                                        <label htmlFor="points">Points</label>
                                        <input type="number" name="points" id="points"
                                               className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                               {...register(`previousResults.${index}.points`, {
                                                   valueAsNumber: true,
                                               })}/>
                                    </div>

                                    <div className="md:col-span-1">
                                        <label htmlFor="opponentPoints">Opponent Points</label>
                                        <input type="number" name="opponentPoints" id="opponentPoints"
                                               className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                                               {...register(`previousResults.${index}.opponentPoints`, {
                                                   valueAsNumber: true,
                                               })}/>
                                    </div>
                                    <div className="btn-box">
                                        {previousResultsFields.length !== 1 && <button
                                            className="mt-6 bg-white-500 hover:bg-blue-700 text-white font-bold py-2
                                            px-4 rounded"
                                            onClick={() => removePreviousResult(index)}>
                                            <svg className="h-4 w-4 text-red-500" width="24" height="24"
                                                 viewBox="0 0 24 24"
                                                 stroke="currentColor" fill="none">
                                                <path stroke="none" d="M0 0h24v24H0z"/>
                                                <line x1="4" y1="7" x2="20" y2="7"/>
                                                <line x1="10" y1="11" x2="10" y2="17"/>
                                                <line x1="14" y1="11" x2="14" y2="17"/>
                                                <path d="M5 7l1 12a2 2 0 0 0 2 2h8a2 2 0 0 0 2 -2l1 -12"/>
                                                <path d="M9 7v-3a1 1 0 0 1 1 -1h4a1 1 0 0 1 1 1v3"/>
                                            </svg>
                                        </button>}
                                        {previousResultsFields.length - 1 === index && <button
                                            className="mt-6 bg-white-500 hover:bg-blue-700 text-white font-bold py-2
                                            px-4 rounded"
                                            onClick={() => appendPreviousResults({
                                                opponentName: '',
                                                points: null,
                                                opponentPoints: null
                                            })}>
                                            <svg className="h-4 w-4 text-green-500" width="24" height="24"
                                                 viewBox="0 0 24 24" stroke="currentColor" fill="none">
                                                <path stroke="none" d="M0 0h24v24H0z"/>
                                                <line x1="12" y1="5" x2="12" y2="19"/>
                                                <line x1="5" y1="12" x2="19" y2="12"/>
                                            </svg>
                                        </button>}
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}

                    <hr className="mt-8"/>
                    <div className="flex-display">
                        <div className="flex-1"></div>
                        <button onClick={cancel}
                                className="rounded-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 right
                            mt-4 rounded">Cancel
                        </button>
                        <button
                            className="rounded-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 right
                            mt-4 rounded">Save
                            changes
                        </button>
                    </div>

                </div>
            </div>
        </form>
    );
}

Details.propTypes = {};

Details.defaultProps = {};

export default Details;
