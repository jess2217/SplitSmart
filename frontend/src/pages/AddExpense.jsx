import { useState } from "react";
import { ArrowLeft, Calculator } from "lucide-react";

function AddExpense({
    group,
    members,
    onBack,
    onSuccess
}) {

    const [description, setDescription] =
        useState("");

    const [amount, setAmount] =
        useState("");

    const [payerId, setPayerId] =
        useState("");

    const [category, setCategory] =
        useState("FOOD");

    const [customCategory, setCustomCategory] =
        useState("");

    const [splitType, setSplitType] =
        useState("EQUAL");

    const [selectedMembers, setSelectedMembers] =
        useState([]);

    const [values, setValues] =
        useState({});

    const [error, setError] =
        useState("");

    const [saving, setSaving] =
        useState(false);

    function toggleMember(id) {

        setSelectedMembers((current) => {

            if (current.includes(id)) {
                return current.filter(
                    (memberId) =>
                        memberId !== id
                );
            }

            return [...current, id];
        });
    }

    function updateValue(id, value) {

        setValues((current) => ({
            ...current,
            [id]: Number(value)
        }));
    }

    async function submit(event) {

        event.preventDefault();

        setError("");

        if (!description.trim()) {
            setError("Enter an expense description.");
            return;
        }
        if (
            category === "OTHER" &&
            !customCategory.trim()
        ) {
            setError(
                "Enter a custom category."
            );
            return;
        }

        if (!amount || Number(amount) <= 0) {
            setError("Enter a valid amount.");
            return;
        }

        if (!payerId) {
            setError("Select who paid.");
            return;
        }

        if (selectedMembers.length === 0) {
            setError(
                "Select at least one participant."
            );
            return;
        }

        if (
            splitType === "EXACT" ||
            splitType === "PERCENTAGE"
        ) {

            const missing =
                selectedMembers.some(
                    (id) =>
                        values[id] === undefined ||
                        values[id] === null
                );

            if (missing) {
                setError(
                    "Enter a value for every participant."
                );
                return;
            }
        }

        const formattedValues = {};

        selectedMembers.forEach((id) => {
            formattedValues[id] =
                Number(values[id] || 0);
        });

        try {

            setSaving(true);

            await onSuccess({

                description:
                    description.trim(),

                amount:
                    Number(amount),

                payerId:
                    Number(payerId),

                participantIds:
                    selectedMembers.map(Number),

                
                category:
                    category === "OTHER"
                        ? customCategory.trim()
                        : category,

                splitType,

                values:
                    formattedValues
            });

        } catch (err) {

            setError(
                err.message ||
                "Unable to add expense."
            );

        } finally {

            setSaving(false);
        }
    }

    return (
        <div className="page narrow-page">

            <button
                className="back-button"
                onClick={onBack}
            >
                <ArrowLeft size={17} />
                Back
            </button>

            <div className="page-header">

                <div>

                    <p className="eyebrow">
                        NEW EXPENSE
                    </p>

                    <h1>
                        Add an expense
                    </h1>

                    <p className="page-description">
                        Add a shared expense to{" "}
                        <strong>
                            {group?.name}
                        </strong>
                    </p>

                </div>

            </div>

            <form
                className="expense-form"
                onSubmit={submit}
            >

                <div className="form-section">

                    <label>
                        Description
                    </label>

                    <input
                        value={description}
                        onChange={(e) =>
                            setDescription(
                                e.target.value
                            )
                        }
                        placeholder="Dinner, cab, hotel..."
                    />

                </div>

                <div className="form-row-two">

                    <div className="form-section">

                        <label>
                            Amount
                        </label>

                        <div className="money-input">

                            <span>₹</span>

                            <input
                                type="number"
                                min="0"
                                step="0.01"
                                value={amount}
                                onChange={(e) =>
                                    setAmount(
                                        e.target.value
                                    )
                                }
                                placeholder="0.00"
                            />

                        </div>

                    </div>

                    <div className="form-section">

    <label>
        Category
    </label>
 <select
        value={category}
        onChange={(e) => {

            const value =
                e.target.value;

            setCategory(value);

            if (value !== "OTHER") {
                setCustomCategory("");
            }

        }}
    >

        <option value="FOOD">
            Food
        </option>

        <option value="TRAVEL">
            Travel
        </option>

        <option value="CAB">
            Cab
        </option>

        <option value="RENT">
            Rent
        </option>

        <option value="HOSTEL">
            Hostel
        </option>

        <option value="GROCERIES">
            Groceries
        </option>

        <option value="ENTERTAINMENT">
            Entertainment
        </option>

        <option value="ACADEMICS">
            Academics
        </option>

        <option value="OTHER">
            Other
        </option>

    </select>

    {category === "OTHER" && (

        <input
            type="text"
            value={customCategory}
            onChange={(e) =>
                setCustomCategory(
                    e.target.value
                )
            }
            placeholder="Enter custom category"
            className="custom-category-input"
        />

    )}

</div>


                </div>

                <div className="form-section">

                    <label>
                        Paid by
                    </label>

                    <select
                        value={payerId}
                        onChange={(e) =>
                            setPayerId(
                                e.target.value
                            )
                        }
                    >

                        <option value="">
                            Select payer
                        </option>

                        {members.map((member) => (

                            <option
                                key={member.id}
                                value={member.id}
                            >
                                {member.name}
                            </option>

                        ))}

                    </select>

                </div>

                <div className="form-section">

                    <label>
                        Split type
                    </label>

                    <div className="split-options">

                        {[
                            ["EQUAL", "Equal"],
                            ["EXACT", "Exact"],
                            ["PERCENTAGE", "Percentage"]
                        ].map(
                            ([value, label]) => (

                                <button
                                    type="button"
                                    key={value}
                                    className={
                                        `split-option ${
                                            splitType === value
                                                ? "selected"
                                                : ""
                                        }`
                                    }
                                    onClick={() =>
                                        setSplitType(
                                            value
                                        )
                                    }
                                >
                                    <Calculator
                                        size={17}
                                    />

                                    {label}
                                </button>

                            )
                        )}

                    </div>

                </div>

                <div className="form-section">

                    <label>
                        Participants
                    </label>

                    <div className="participant-list">

                        {members.map((member) => {

                            const selected =
                                selectedMembers.includes(
                                    member.id
                                );

                            return (
                                <div
                                    className={`participant ${
                                        selected
                                            ? "selected"
                                            : ""
                                    }`}
                                    key={member.id}
                                    onClick={() =>
                                        toggleMember(
                                            member.id
                                        )
                                    }
                                >

                                    <div className="participant-check">
                                        {selected
                                            ? "✓"
                                            : ""}
                                    </div>

                                    <div className="member-avatar small">
                                        {member.name
                                            ?.charAt(0)
                                            ?.toUpperCase()}
                                    </div>

                                    <span>
                                        {member.name}
                                    </span>

                                    {selected &&
                                        splitType !==
                                            "EQUAL" && (

                                            <input
                                                className="share-value"
                                                type="number"
                                                min="0"
                                                step="0.01"
                                                placeholder={
                                                    splitType ===
                                                    "PERCENTAGE"
                                                        ? "%"
                                                        : "₹"
                                                }
                                                onClick={(e) =>
                                                    e.stopPropagation()
                                                }
                                                onChange={(e) =>
                                                    updateValue(
                                                        member.id,
                                                        e.target.value
                                                    )
                                                }
                                            />

                                        )}

                                </div>
                            );
                        })}

                    </div>

                </div>

                {error && (
                    <div className="form-error">
                        {error}
                    </div>
                )}

                <div className="form-actions">

                    <button
                        type="button"
                        className="secondary-button"
                        onClick={onBack}
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        className="primary-button"
                        disabled={saving}
                    >
                        {saving
                            ? "Saving..."
                            : "Add Expense"}
                    </button>

                </div>

            </form>

        </div>
    );
}

export default AddExpense;