import {
    IndianRupee,
    Users,
    Receipt,
    ArrowRight,
    Wallet,
    Activity,
    UserPlus
} from "lucide-react";

function Dashboard({
    groups,
    groupData,
    currentUser,
    onOpenGroup
}) {

    // =========================================
    // CALCULATE DASHBOARD DATA
    // =========================================

    const allExpenses =
        Object.values(groupData || {})
            .flatMap(
                (data) =>
                    data?.expenses || []
            );

    const totalSpent =
        allExpenses.reduce(
            (sum, expense) =>
                sum +
                Number(expense.amount || 0),
            0
        );

    const totalExpenseCount =
        allExpenses.length;

    const totalMembers =
        Object.values(groupData || {})
            .reduce(
                (total, data) =>
                    total +
                    (data?.members?.length || 0),
                0
            );

    // =========================================
    // RECENT EXPENSES
    // =========================================

    const recentExpenses =
        [...allExpenses]
            .sort(
                (a, b) =>
                    new Date(
                        b.dateTime || 0
                    ) -
                    new Date(
                        a.dateTime || 0
                    )
            )
            .slice(0, 5);

    // =========================================
    // GROUP SPENDING
    // =========================================

    const groupSummaries =
        groups.map((group) => {

            const data =
                groupData[group.id] || {};

            const expenses =
                data.expenses || [];

            const members =
                data.members || [];

            const spent =
                expenses.reduce(
                    (sum, expense) =>
                        sum +
                        Number(
                            expense.amount || 0
                        ),
                    0
                );

            return {
                ...group,
                members,
                expenses,
                spent
            };
        });

    const maxGroupSpent =
        Math.max(
            ...groupSummaries.map(
                (group) =>
                    group.spent
            ),
            1
        );

    // =========================================
    // FORMAT DATE
    // =========================================

    function formatDate(date) {

        if (!date) {
            return "";
        }

        const parsed =
            new Date(date);

        if (
            Number.isNaN(
                parsed.getTime()
            )
        ) {
            return "";
        }

        return parsed.toLocaleDateString(
            "en-IN",
            {
                day: "2-digit",
                month: "short",
                year: "numeric"
            }
        );
    }


    // =========================================
    // GET EXPENSE GROUP
    // =========================================

    function getExpenseGroup(expense) {

        for (
            const group of groups
        ) {

            const expenses =
                groupData[
                    group.id
                ]?.expenses || [];

            if (
                expenses.some(
                    (item) =>
                        item.id ===
                        expense.id
                )
            ) {
                return group;
            }
        }

        return null;
    }


    // =========================================
    // GET CATEGORY ICON
    // =========================================

    function getCategoryIcon(
        category
    ) {

        switch (category) {

            case "FOOD":
                return "🍴";

            case "TRAVEL":
                return "✈️";

            case "SHOPPING":
                return "🛒";

            case "TRANSPORT":
                return "🚕";

            case "ENTERTAINMENT":
                return "🎬";

            default:
                return "₹";
        }
    }


    // =========================================
    // EMPTY STATE
    // =========================================

    if (groups.length === 0) {

        return (
            <div className="page dashboard-page">

                <div className="dashboard-welcome">

                    <p className="eyebrow">
                        OVERVIEW
                    </p>

                  <h1>
    Good to see you, {currentUser?.name || "User"}
</h1>

                    <p className="page-description">
                        Here's what's happening
                        with your shared expenses.
                    </p>

                </div>


                <div className="dashboard-empty">

                    <div className="dashboard-empty-icon">
                        <Wallet size={28} />
                    </div>

                    <h2>
                        Your dashboard is empty
                    </h2>

                    <p>
                        Create a group from the
                        Groups section to start
                        tracking shared expenses.
                    </p>

                </div>

            </div>
        );
    }


    // =========================================
    // DASHBOARD
    // =========================================

    return (

        <div className="page dashboard-page">


            {/* =================================
                WELCOME
                ================================= */}

            <div className="dashboard-welcome">

                <div>

                    <p className="eyebrow">
                        OVERVIEW
                    </p>

                   <h1>
    Good to see you, {currentUser?.name || "User"}
</h1>

                    <p className="page-description">
                        Here's what's happening
                        with your shared expenses.
                    </p>

                </div>

            </div>


            {/* =================================
                KPI CARDS
                ================================= */}

            <div className="dashboard-kpi-grid">


                {/* TOTAL SPENT */}

                <div className="dashboard-kpi-card">

                    <div className="dashboard-kpi-icon purple">
                        <IndianRupee size={20} />
                    </div>

                    <span>
                        TOTAL SPENT
                    </span>

                    <strong>
                        ₹{totalSpent.toFixed(2)}
                    </strong>

                    <small>
                        Across all groups
                    </small>

                </div>


                {/* GROUPS */}

                <div className="dashboard-kpi-card">

                    <div className="dashboard-kpi-icon green">
                        <Users size={20} />
                    </div>

                    <span>
                        GROUPS
                    </span>

                    <strong>
                        {groups.length}
                    </strong>

                    <small>
                        Active groups
                    </small>

                </div>


                {/* MEMBERS */}

                <div className="dashboard-kpi-card">

                    <div className="dashboard-kpi-icon orange">
                        <UserPlus size={20} />
                    </div>

                    <span>
                        MEMBERS
                    </span>

                    <strong>
                        {totalMembers}
                    </strong>

                    <small>
                        Total members
                    </small>

                </div>


                {/* EXPENSES */}

                <div className="dashboard-kpi-card">

                    <div className="dashboard-kpi-icon blue">
                        <Receipt size={20} />
                    </div>

                    <span>
                        EXPENSES
                    </span>

                    <strong>
                        {totalExpenseCount}
                    </strong>

                    <small>
                        Total expenses
                    </small>

                </div>

            </div>


            {/* =================================
                MAIN DASHBOARD GRID
                ================================= */}

            <div className="dashboard-main-grid">


                {/* =================================
                    RECENT EXPENSES
                    ================================= */}

                <section className="dashboard-panel">

                    <div className="dashboard-panel-header">

                        <div>

                            <p className="eyebrow">
                                ACTIVITY
                            </p>

                            <h2>
                                Recent Expenses
                            </h2>

                        </div>

                        <Activity size={18} />

                    </div>


                    {recentExpenses.length === 0 ? (

                        <div className="dashboard-panel-empty">
                            No expenses yet.
                        </div>

                    ) : (

                        <div className="dashboard-expense-list">

                            {recentExpenses.map(
                                (expense) => {

                                    const group =
                                        getExpenseGroup(
                                            expense
                                        );

                                    return (

                                        <div
                                            className="dashboard-expense-row"
                                            key={
                                                expense.id
                                            }
                                        >

                                            <div className="dashboard-expense-icon">
                                                {
                                                    getCategoryIcon(
                                                        expense.category
                                                    )
                                                }
                                            </div>


                                            <div className="dashboard-expense-info">

                                                <strong>
                                                    {
                                                        expense.description ||
                                                        "Expense"
                                                    }
                                                </strong>

                                                <span>

                                                    Paid by{" "}

                                                    {
                                                        expense
                                                            .payer
                                                            ?.name ||
                                                        "Unknown"
                                                    }

                                                    {group &&
                                                        ` • ${group.name}`}

                                                </span>

                                                <small>
                                                    {
                                                        formatDate(
                                                            expense.dateTime
                                                        )
                                                    }
                                                </small>

                                            </div>


                                            <strong className="dashboard-expense-amount">
                                                ₹
                                                {Number(
                                                    expense.amount ||
                                                    0
                                                ).toFixed(2)}
                                            </strong>

                                        </div>
                                    );
                                }
                            )}

                        </div>
                    )}


                    {allExpenses.length > 5 && (

                        <div className="dashboard-panel-footer">

                            <span>
                                Showing latest 5
                            </span>

                        </div>
                    )}

                </section>


                {/* =================================
                    GROUP OVERVIEW
                    ================================= */}

                <section className="dashboard-panel">

                    <div className="dashboard-panel-header">

                        <div>

                            <p className="eyebrow">
                                GROUPS
                            </p>

                            <h2>
                                Your Groups Overview
                            </h2>

                        </div>

                        <Users size={18} />

                    </div>


                    <div className="dashboard-group-list">

                        {groupSummaries
                            .slice(0, 5)
                            .map((group) => {

                                const percentage =
                                    Math.min(
                                        100,
                                        (
                                            group.spent /
                                            maxGroupSpent
                                        ) *
                                        100
                                    );

                                return (

                                    <div
                                        className="dashboard-group-row"
                                        key={group.id}
                                    >

                                        <div className="dashboard-group-avatar">
                                            {
                                                group.name
                                                    ?.charAt(
                                                        0
                                                    )
                                                    ?.toUpperCase()
                                            }
                                        </div>


                                        <div className="dashboard-group-info">

                                            <strong>
                                                {
                                                    group.name
                                                }
                                            </strong>

                                            <span>
                                                {
                                                    group
                                                        .members
                                                        .length
                                                }{" "}
                                                {group
                                                    .members
                                                    .length ===
                                                1
                                                    ? "member"
                                                    : "members"}
                                            </span>


                                            <div className="dashboard-progress">

                                                <div
                                                    style={{
                                                        width:
                                                            `${percentage}%`
                                                    }}
                                                />

                                            </div>

                                            <small>
                                                ₹
                                                {group.spent.toFixed(
                                                    2
                                                )}{" "}
                                                spent
                                            </small>

                                        </div>


                                        <button
                                            type="button"
                                            className="dashboard-group-arrow"
                                            onClick={() =>
                                                onOpenGroup(
                                                    group.id
                                                )
                                            }
                                            title="Open group"
                                        >
                                            <ArrowRight
                                                size={17}
                                            />
                                        </button>

                                    </div>
                                );
                            })}

                    </div>

                </section>

            </div>


            {/* =================================
                RECENT ACTIVITY
                ================================= */}

            <section className="dashboard-activity-panel">

                <div className="dashboard-panel-header">

                    <div>

                        <p className="eyebrow">
                            TIMELINE
                        </p>

                        <h2>
                            Recent Activity
                        </h2>

                    </div>

                    <Activity size={18} />

                </div>


                <div className="dashboard-activity-grid">

                    {recentExpenses
                        .slice(0, 3)
                        .map((expense) => {

                            const group =
                                getExpenseGroup(
                                    expense
                                );

                            return (

                                <div
                                    className="dashboard-activity-item"
                                    key={
                                        expense.id
                                    }
                                >

                                    <div className="dashboard-activity-icon">
                                        <Receipt
                                            size={17}
                                        />
                                    </div>

                                    <div>

                                        <strong>
                                            {
                                                expense
                                                    .payer
                                                    ?.name ||
                                                "Someone"
                                            }{" "}
                                            added an expense
                                        </strong>

                                        <span>
                                            {
                                                expense
                                                    .description ||
                                                "Expense"
                                            }{" "}
                                            of ₹
                                            {Number(
                                                expense.amount ||
                                                0
                                            ).toFixed(
                                                2
                                            )}
                                        </span>

                                        <small>
                                            {
                                                formatDate(
                                                    expense.dateTime
                                                )
                                            }

                                            {group &&
                                                ` • ${group.name}`}
                                        </small>

                                    </div>

                                </div>
                            );
                        })}

                    {recentExpenses.length === 0 && (

                        <div className="dashboard-panel-empty">
                            No recent activity.
                        </div>

                    )}

                </div>

            </section>

        </div>
    );
}

export default Dashboard;