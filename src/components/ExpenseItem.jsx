import {
    Utensils,
    Car,
    Home,
    ShoppingBag,
    Gamepad2,
    Receipt,
    Trash2
} from "lucide-react";

function ExpenseItem({
    expense,
    groupId,
    onDelete
}) {

    const icons = {
        FOOD: Utensils,
        CAB: Car,
        TRAVEL: Car,
        HOSTEL: Home,
        RENT: Home,
        GROCERIES: ShoppingBag,
        ENTERTAINMENT: Gamepad2
    };

    const Icon =
        icons[expense.category] || Receipt;

    function handleDelete() {

        const confirmed = window.confirm(
            `Are you sure you want to delete "${expense.description}"?`
        );

        if (!confirmed) {
            return;
        }

        onDelete(
            groupId,
            expense.id
        );
    }

    return (
        <div className="expense-item">

            <div className="expense-icon">
                <Icon size={18} />
            </div>

            <div className="expense-info">

                <strong>
                    {expense.description}
                </strong>

                <span>
                    {expense.category || "OTHER"}
                </span>

            </div>

            <div className="expense-amount">
                ₹{Number(
                    expense.amount || 0
                ).toFixed(2)}
            </div>

            <button
                type="button"
                className="expense-delete-button"
                onClick={handleDelete}
                title="Delete expense"
                aria-label="Delete expense"
            >
                <Trash2 size={16} />
            </button>

        </div>
    );
}

export default ExpenseItem;