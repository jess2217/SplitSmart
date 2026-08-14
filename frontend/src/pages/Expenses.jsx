import { useState } from "react";

import {
    Receipt,
    Utensils,
    Car,
    Home,
    ShoppingCart,
    GraduationCap,
    Gamepad2,
    Zap,
    MoreHorizontal,
    Trash2
} from "lucide-react";


function getCategoryIcon(category) {

    switch (category) {

        case "FOOD":
            return <Utensils size={20} />;

        case "TRAVEL":
        case "CAB":
            return <Car size={20} />;

        case "RENT":
        case "HOSTEL":
            return <Home size={20} />;

        case "GROCERIES":
        case "MESS":
            return <ShoppingCart size={20} />;

        case "ACADEMICS":
        case "COLLEGE_EVENT":
        case "STATIONERY":
            return <GraduationCap size={20} />;

        case "ENTERTAINMENT":
            return <Gamepad2 size={20} />;

        case "UTILITIES":
            return <Zap size={20} />;

        default:
            return <MoreHorizontal size={20} />;
    }
}


function Expenses({
    groups = [],
    groupData = {},
    selectedGroupId,
    onOpenGroup,
    onDeleteExpense
}) {

    /*
     * Controls the custom delete confirmation popup.
     */
    const [
        deleteExpenseData,
        setDeleteExpenseData
    ] = useState(null);


    let expenseGroups = [];


    /*
     * If a group is selected,
     * show only that group.
     */
    if (selectedGroupId) {

        const group = groups.find(
            (item) =>
                item.id === selectedGroupId
        );

        if (group) {

            expenseGroups = [
                {
                    group: group,
                    expenses:
                        groupData[group.id]?.expenses || []
                }
            ];
        }

    } else {

        /*
         * Otherwise show all groups.
         */
        expenseGroups = groups.map(
            (group) => ({
                group: group,
                expenses:
                    groupData[group.id]?.expenses || []
            })
        );
    }


    /*
     * Calculate total expenses.
     */
    const totalExpenses =
        expenseGroups.reduce(
            (total, item) => {

                const groupTotal =
                    item.expenses.reduce(
                        (sum, expense) =>
                            sum +
                            Number(
                                expense.amount || 0
                            ),
                        0
                    );

                return total + groupTotal;
            },
            0
        );


    /*
     * Open the delete confirmation popup.
     */
    function openDeletePopup(
        groupId,
        expenseId,
        description
    ) {

        setDeleteExpenseData({
            groupId,
            expenseId,
            description
        });
    }


    /*
     * Actually delete the expense.
     */
    async function handleDeleteExpense() {

        if (!deleteExpenseData) {
            return;
        }

        const {
            groupId,
            expenseId
        } = deleteExpenseData;


        console.log(
            "DELETE CLICKED"
        );

        console.log(
            "Group ID:",
            groupId
        );

        console.log(
            "Expense ID:",
            expenseId
        );


        if (
            typeof onDeleteExpense !==
            "function"
        ) {

            console.error(
                "ERROR: onDeleteExpense is not connected!"
            );

            alert(
                "Delete function is not connected."
            );

            return;
        }


        try {

            await onDeleteExpense(
                groupId,
                expenseId
            );


            console.log(
                "Expense deleted successfully."
            );


            /*
             * Close popup only after
             * successful deletion.
             */
            setDeleteExpenseData(null);

        } catch (error) {

            console.error(
                "Delete failed:",
                error
            );

            alert(
                error?.message ||
                "Failed to delete expense."
            );
        }
    }


    return (
        <div className="page">

            {/* =========================
                HEADER
            ========================= */}

            <div className="page-header">

                <div>

                    <p className="eyebrow">
                        ACTIVITY
                    </p>

                    <h1>
                        Expenses
                    </h1>

                    <p className="page-description">
                        Track and manage your group expenses.
                    </p>

                </div>


                <div className="expense-total-card">

                    <span>
                        Total Expenses
                    </span>

                    <strong>
                        ₹{totalExpenses.toFixed(2)}
                    </strong>

                </div>

            </div>


            {/* =========================
                NO GROUPS
            ========================= */}

            {expenseGroups.length === 0 && (

                <div className="empty-state">

                    <Receipt size={48} />

                    <h2>
                        No groups yet
                    </h2>

                    <p>
                        Create a group to start
                        tracking expenses.
                    </p>

                </div>

            )}


            {/* =========================
                GROUPS
            ========================= */}

            {expenseGroups.map(
                ({ group, expenses }) => (

                    <section
                        className="expenses-section"
                        key={group.id}
                    >

                        {/* GROUP HEADER */}

                        <div className="section-header">

                            <div>

                                <p className="eyebrow">
                                    GROUP
                                </p>

                                <h2>
                                    {group.name}
                                </h2>

                                <span>
                                    {expenses.length}{" "}
                                    {expenses.length === 1
                                        ? "expense"
                                        : "expenses"}
                                </span>

                            </div>


                            {onOpenGroup && (

                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={() =>
                                        onOpenGroup(
                                            group.id
                                        )
                                    }
                                >
                                    View Group
                                </button>

                            )}

                        </div>


                        {/* =========================
                            NO EXPENSES
                        ========================= */}

                        {expenses.length === 0 ? (

                            <div className="empty-expenses">

                                <Receipt size={32} />

                                <p>
                                    No expenses have been
                                    added to this group yet.
                                </p>

                            </div>

                        ) : (

                            <div className="expense-grid">

                                {expenses.map(
                                    (expense) => (

                                        <div
                                            className="expense-card"
                                            key={expense.id}
                                        >

                                            {/* CATEGORY */}

                                            <div className="expense-card-top">

                                                <div className="category-icon">

                                                    {getCategoryIcon(
                                                        expense.category
                                                    )}

                                                </div>

                                                <div className="expense-category">

                                                    {expense.category ||
                                                        "OTHER"}

                                                </div>

                                            </div>


                                            {/* DESCRIPTION */}

                                            <div className="expense-description">

                                                {expense.description}

                                            </div>


                                            {/* AMOUNT */}

                                            <div className="expense-amount">

                                                ₹
                                                {Number(
                                                    expense.amount || 0
                                                ).toFixed(2)}

                                            </div>


                                            {/* DETAILS */}

                                            <div className="expense-details">

                                                <div>

                                                    <span>
                                                        Paid by
                                                    </span>

                                                    <strong>
                                                        {expense.payer?.name ||
                                                            "Unknown"}
                                                    </strong>

                                                </div>


                                                <div>

                                                    <span>
                                                        Split
                                                    </span>

                                                    <strong>
                                                        {expense.splitType ||
                                                            "UNKNOWN"}
                                                    </strong>

                                                </div>

                                            </div>


                                            {/* DATE */}

                                            {expense.dateTime && (

                                                <div className="expense-date">

                                                    {new Date(
                                                        expense.dateTime
                                                    ).toLocaleString(
                                                        "en-IN",
                                                        {
                                                            dateStyle:
                                                                "medium",
                                                            timeStyle:
                                                                "short"
                                                        }
                                                    )}

                                                </div>

                                            )}


                                            {/* SPLIT DETAILS */}

                                            {expense.shares &&
                                                Object.keys(
                                                    expense.shares
                                                ).length > 0 && (

                                                    <div className="expense-shares">

                                                        <h4>
                                                            Split Details
                                                        </h4>


                                                        {Object.entries(
                                                            expense.shares
                                                        ).map(
                                                            (
                                                                [
                                                                    student,
                                                                    amount
                                                                ],
                                                                index
                                                            ) => (

                                                                <div
                                                                    className="share-row"
                                                                    key={index}
                                                                >

                                                                    <span>
                                                                        {extractStudentName(
                                                                            student
                                                                        )}
                                                                    </span>

                                                                    <strong>
                                                                        ₹
                                                                        {Number(
                                                                            amount || 0
                                                                        ).toFixed(2)}
                                                                    </strong>

                                                                </div>

                                                            )
                                                        )}

                                                    </div>

                                                )}


                                            {/* =========================
                                                DELETE BUTTON
                                            ========================= */}

                                            <button
                                                type="button"
                                                className="expense-delete-button"
                                                onClick={() =>
                                                    openDeletePopup(
                                                        group.id,
                                                        expense.id,
                                                        expense.description
                                                    )
                                                }
                                            >

                                                <Trash2
                                                    size={16}
                                                />

                                                Delete Expense

                                            </button>

                                        </div>

                                    )
                                )}

                            </div>

                        )}

                    </section>

                )
            )}


            {/* =========================
                DELETE CONFIRMATION MODAL
            ========================= */}

            {deleteExpenseData && (

                <div
                    className="delete-modal-overlay"
                    onClick={() =>
                        setDeleteExpenseData(null)
                    }
                >

                    <div
                        className="delete-modal"
                        onClick={(event) =>
                            event.stopPropagation()
                        }
                    >

                        <h3>
                            Delete Expense?
                        </h3>

                        <p>

                            Are you sure you want
                            to delete{" "}

                            <strong>
                                "{deleteExpenseData.description}"
                            </strong>
                            ?

                        </p>


                        <div className="delete-modal-actions">

                            <button
                                type="button"
                                className="delete-cancel-button"
                                onClick={() =>
                                    setDeleteExpenseData(
                                        null
                                    )
                                }
                            >
                                Cancel
                            </button>


                            <button
                                type="button"
                                className="delete-confirm-button"
                                onClick={
                                    handleDeleteExpense
                                }
                            >

                                <Trash2 size={16} />

                                Yes, Delete

                            </button>

                        </div>

                    </div>

                </div>

            )}

        </div>
    );
}


/*
 * Convert:
 *
 * ID: 1 | Name: Aprajita | Email: ...
 *
 * into:
 *
 * Aprajita
 */
function extractStudentName(
    studentString
) {

    if (!studentString) {
        return "Unknown";
    }


    const match =
        studentString.match(
            /Name:\s*([^|]+)/
        );


    if (match) {
        return match[1].trim();
    }


    return studentString;
}


export default Expenses;