<template>
    <div class="sidebar" :class="{ small: small }">
        <div class="entries">
            <!-- Rides -->
            <SidebarEntry
                :title="$t('sidebar.rides')"
                icon="fa-biking"
                :icon-color="'#156ec7'"
                :is-small="small"
                :class="{ selected: value === config.viewModes.RIDES }"
                @entryClicked="switchToView(config.viewModes.RIDES)"
            >
                <div class="field">
                    <b-radio
                        v-model="computedSubViewMode"
                        :native-value="
                            subViewMode === config.subViewModes.RIDES_ORIGINAL
                                ? config.subViewModes.RIDES_ORIGINAL
                                : config.subViewModes.RIDES_DENSITY_ALL
                        "
                    >
                        {{ $t("ride.all") }}
                    </b-radio>
                </div>
                <div class="field">
                    <b-radio
                        v-model="computedSubViewMode"
                        :native-value="
                            config.subViewModes.RIDES_DENSITY_RUSHHOURMORNING
                        "
                    >
                        {{ $t("ride.rushHourMorning") }}
                    </b-radio>
                </div>
                <div class="field">
                    <b-radio
                        v-model="computedSubViewMode"
                        :native-value="
                            config.subViewModes.RIDES_DENSITY_RUSHHOUREVENING
                        "
                    >
                        {{ $t("ride.rushHourEvening") }}
                    </b-radio>
                </div>
                <div class="field">
                    <b-radio
                        v-model="computedSubViewMode"
                        :native-value="
                            config.subViewModes.RIDES_DENSITY_WEEKEND
                        "
                    >
                        {{ $t("ride.weekend") }}
                    </b-radio>
                </div>
            </SidebarEntry>

            <!-- Incidents -->
            <SidebarEntry
                :title="$t('sidebar.incidents')"
                icon="fa-car-crash"
                :icon-color="'#1d917c'"
                :is-small="small"
                :class="{ selected: value === config.viewModes.INCIDENTS }"
                @entryClicked="switchToView(config.viewModes.INCIDENTS)"
            >
                <div class="entry-subtext">
                    {{ $t("sidebar.incidentsDescription") }}
                </div>
            </SidebarEntry>

            <!-- Surface quality -->
            <SidebarEntry
                :title="$t('sidebar.surfaceQuality')"
                icon="fa-road"
                :icon-color="'#d63e12'"
                :is-small="small"
                :class="{
                    selected: value === config.viewModes.SURFACE_QUALITY
                }"
                @entryClicked="switchToView(config.viewModes.SURFACE_QUALITY)"
            >
                <div class="entry-subtext">
                    {{ $t("sidebar.surfaceQualityDescription") }}
                </div>
            </SidebarEntry>

            <!-- Speed -->
            <SidebarEntry
                :title="$t('sidebar.relativeSpeed')"
                icon="fa-tachometer-alt"
                :icon-color="'#156ec7'"
                :is-small="small"
                :class="{ selected: value === config.viewModes.RELATIVE_SPEED }"
                @entryClicked="switchToView(config.viewModes.RELATIVE_SPEED)"
            >
                <div class="entry-subtext">
                    {{ $t("sidebar.relativeSpeedDescription") }}
                </div>
            </SidebarEntry>

            <!-- Stop times -->
            <SidebarEntry
                :title="$t('sidebar.stopTimes')"
                icon="fa-traffic-light"
                :icon-color="'#1d917c'"
                :is-small="small"
                :class="{ selected: value === config.viewModes.STOP_TIMES }"
                @entryClicked="switchToView(config.viewModes.STOP_TIMES)"
            >
                <div class="entry-subtext">
                    {{ $t("sidebar.stopTimesDescription") }}
                </div>
            </SidebarEntry>

            <!-- Popularity of street segments -->
            <SidebarEntry
                :title="$t('sidebar.popularity')"
                :icon="'fa-tools'"
                :icon-color="'#d63e12'"
                :is-small="small"
                :class="{ selected: value === config.viewModes.POPULARITY }"
                @entryClicked="switchToView(config.viewModes.POPULARITY)"
            >
                <div class="entry-subtext">
                    {{ $t("sidebar.popularityDescription") }}
                </div>

                <div class="field" style="margin-top: 10px">
                    <b-checkbox
                        v-model="checked"
                        value="true"
                        unchecked-value="false"
                        @input="$emit('update:incidents-visible', checked)">
                        {{ $t("popularity.showIncidents") }}
                    </b-checkbox>
                </div>
            </SidebarEntry>

            <!-- Box analysis -->
            <SidebarEntry
                :title="$t('sidebar.boxAnalysis')"
                icon="fa-draw-polygon"
                :icon-color="'#156ec7'"
                :is-small="small"
                :class="{ selected: value === config.viewModes.BOX_ANALYSIS }"
                @entryClicked="
                    switchToView(config.viewModes.BOX_ANALYSIS, 1 << 0)
                "
            >
                <div class="entry-subtext">
                    {{ $t("sidebar.boxAnalysisDescription") }}
                </div>

                <div class="field" style="margin-top: 10px">
                    <b-checkbox v-model="computedBoxAnalysisHelper1">
                        {{ $t("boxAnalysis.all") }}
                    </b-checkbox>
                </div>
                <div class="field">
                    <b-checkbox v-model="computedBoxAnalysisHelper2">
                        {{ $t("boxAnalysis.start") }}
                    </b-checkbox>
                </div>
                <div class="field">
                    <b-checkbox v-model="computedBoxAnalysisHelper3">
                        {{ $t("boxAnalysis.end") }}
                    </b-checkbox>
                </div>
            </SidebarEntry>

            <!-- Tools -->
            <SidebarEntry
                :title="$t('sidebar.tools')"
                :icon="'fa-tools'"
                :icon-color="'#1d917c'"
                :is-small="small"
                :class="{ selected: value === config.viewModes.TOOLS }"
                @entryClicked="switchToView(config.viewModes.TOOLS)"
            >
                <div class="entry-subtext">
                    {{ $t("sidebar.toolsDescription") }}
                </div>
            </SidebarEntry>
        </div>

        <div
            class="bottom-expand-button"
            @click="small = false"
            :title="$t('sidebar.expandMenu')"
        >
            <i class="fas fa-lg fa-angle-double-right"></i>
        </div>
        <div class="bottom">
            <div
                class="bottom-statistics"
                :title="small ? $t('sidebar.statistics') : ''"
                :class="{ selected: value === config.viewModes.STATISTICS }"
                @click="switchToView(config.viewModes.STATISTICS)"
            >
                <div class="bottom-icon">
                    <i class="fas fa-lg fa-chart-pie"></i>
                </div>
                <div class="bottom-text">
                    {{ $t("sidebar.statistics") }}
                </div>
            </div>
            <div class="bottom-reduce-button" @click="small = true">
                <i class="fas fa-lg fa-angle-double-left"></i>
            </div>
        </div>
    </div>
</template>

<script>
import SidebarEntry from "@/components/SidebarEntry";
import Config from "@/constants";

export default {
    name: "Sidebar",
    components: { SidebarEntry },
    props: ["value", "subViewMode"],
    data() {
        return {
            config: Config,
            small: false,
            checked: false
        };
    },
    methods: {
        switchToView(viewId, defaultSubViewMode = Config.subViewModes.DEFAULT) {
            if (this.value === viewId) {
                this.$emit("input", Config.viewModes.NONE);
            } else {
                this.$emit("input", viewId);
            }

            this.$emit("update:sub-view-mode", defaultSubViewMode);
        }
    },
    watch: {
        small: function(newValue, oldValue) {
            if (newValue !== oldValue) this.$emit("size-changed");
        }
    },
    computed: {
        computedSubViewMode: {
            get() {
                return this.subViewMode;
            },
            set(value) {
                this.$emit("update:sub-view-mode", value);
            }
        },
        computedBoxAnalysisHelper1: {
            get() {
                return (this.subViewMode & (1 << 0)) > 0;
            },
            set(value) {
                this.$emit(
                    "update:sub-view-mode",
                    value
                        ? this.subViewMode | (1 << 0)
                        : this.subViewMode & ~(1 << 0)
                );
            }
        },
        computedBoxAnalysisHelper2: {
            get() {
                return (this.subViewMode & (1 << 1)) > 0;
            },
            set(value) {
                this.$emit(
                    "update:sub-view-mode",
                    value
                        ? this.subViewMode | (1 << 1)
                        : this.subViewMode & ~(1 << 1)
                );
            }
        },
        computedBoxAnalysisHelper3: {
            get() {
                return (this.subViewMode & (1 << 2)) > 0;
            },
            set(value) {
                this.$emit(
                    "update:sub-view-mode",
                    value
                        ? this.subViewMode | (1 << 2)
                        : this.subViewMode & ~(1 << 2)
                );
            }
        }
    }
};
</script>

<style lang="scss">
$sidebar-width: 304px;

.sidebar {
    width: $sidebar-width;
    flex-shrink: 0;
    background-color: #fafafa; //#eaeaea;
    border-right: 1px solid rgba(0, 0, 0, 0.1);
    overflow-y: scroll;
    height: calc(100vh - 57px);
}

.entries {
    padding-top: 8px;
    height: calc(100% - 53px);
    overflow-y: auto;
    overflow-x: hidden;
}

.bottom {
    height: 52px;
    display: flex;
    align-items: center;
    border-top: 1px solid rgba(0, 0, 0, 0.1);
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);

    .bottom-statistics {
        display: flex;
        align-items: center;
        padding: 12px 8px;
        width: 100%;

        &:hover,
        &:active {
            background-color: #eaeaea;
            cursor: pointer;
        }

        .bottom-icon {
            width: 60px;
            flex-shrink: 0;
            padding-right: 6px;
            text-align: center;
            color: slategray;
        }

        .bottom-text {
            width: 100%;
            color: black;
            font-size: 18px;
            font-family: "Segoe UI VSS (Regular)", "Segoe UI", "-apple-system",
                BlinkMacSystemFont, Roboto, "Helvetica Neue", Helvetica, Ubuntu,
                Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji",
                "Segoe UI Symbol";
            user-select: none;
        }

        &.selected {
            font-weight: bold;
            background-color: #dcdcdc !important;
            border-bottom: 1px solid #e0e0e0;

            &::before {
                bottom: 0;
                background-color: rgba(0, 0, 0, 0.2);
                content: "";
                left: 0;
                position: absolute;
                top: 0;
                width: 3px;
            }
        }
    }

    .bottom-reduce-button {
        height: 100%;
        width: 60px;
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;

        &:hover,
        &:active {
            background-color: #eaeaea;
            cursor: pointer;
            color: #156ec7;
        }
    }
}

.bottom-expand-button {
    height: 52px;
    width: 70px;
    display: none;
    align-items: center;
    justify-content: center;

    &:hover,
    &:active {
        background-color: #eaeaea;
        cursor: pointer;
        color: #156ec7;
    }
}

.sidebar.small {
    width: 71px;
    overflow: hidden;

    .entries {
        height: calc(100% - 104px);
    }

    .entry {
        .entry-label .entry-text,
        .entry-content {
            display: none;
        }
    }

    .bottom .bottom-statistics .bottom-text {
        display: none;
    }

    .bottom .bottom-reduce-button {
        display: none;
    }

    .bottom-expand-button {
        display: flex;
    }
}
</style>
