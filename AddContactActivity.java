public class AddContactActivity extends AppCompatActivity {

    private EditText contactEmailEditText;
    private Button addContactButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        contactEmailEditText = findViewById(R.id.editTextContactEmail);
        addContactButton = findViewById(R.id.buttonAddContact);

        // Add Contact Button Listener
        addContactButton.setOnClickListener(v -> addContact());
    }

    private void addContact(){
        String contactEmail = contactEmailEditText.getText().toString().trim();
        if(contactEmail.isEmpty()){
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find user by email
        db.collection("Users")
                .whereEqualTo("email", contactEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(!task.getResult().isEmpty()){
                            DocumentSnapshot userDoc = task.getResult().getDocuments().get(0);
                            String contactId = userDoc.getId();

                            // Add contact to current user's Contacts collection
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            db.collection("Users").document(currentUserId)
                                    .collection("Contacts").document(contactId)
                                    .set(new Contact(contactId, userDoc.getString("name"), contactEmail))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Contact added successfully.", Toast.LENGTH_SHORT).show();
                                        // Optionally, send OTP for verification
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to add contact.", Toast.LENGTH_SHORT).show();
                                    });
                        } else
