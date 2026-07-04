package com.smartcare.clinicsys.controller;

import com.smartcare.clinicsys.dto.*;
import com.smartcare.clinicsys.model.*;
import com.smartcare.clinicsys.repository.*;
import com.smartcare.clinicsys.security.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenService.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                role));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        Role roleVal;
        try {
            roleVal = Role.valueOf(signUpRequest.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Role must be ADMIN, DOCTOR, or PATIENT"));
        }

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .role(roleVal)
                .build();

        User savedUser = userRepository.save(user);

        if (roleVal == Role.PATIENT) {
            Patient patient = Patient.builder()
                    .id(savedUser.getId())
                    .user(savedUser)
                    .name(signUpRequest.getPatientName() != null ? signUpRequest.getPatientName() : savedUser.getUsername())
                    .dateOfBirth(signUpRequest.getDateOfBirth() != null ? signUpRequest.getDateOfBirth() : "2000-01-01")
                    .phone(signUpRequest.getPatientPhone() != null ? signUpRequest.getPatientPhone() : "000-0000000")
                    .medicalHistorySummary(signUpRequest.getMedicalHistorySummary() != null ? signUpRequest.getMedicalHistorySummary() : "")
                    .build();
            patientRepository.save(patient);
        } else if (roleVal == Role.DOCTOR) {
            Doctor doctor = Doctor.builder()
                    .id(savedUser.getId())
                    .user(savedUser)
                    .name(signUpRequest.getDoctorName() != null ? signUpRequest.getDoctorName() : savedUser.getUsername())
                    .specialization(signUpRequest.getSpecialization() != null ? signUpRequest.getSpecialization() : "General Practice")
                    .phone(signUpRequest.getDoctorPhone() != null ? signUpRequest.getDoctorPhone() : "000-0000000")
                    .availability(signUpRequest.getAvailability() != null ? signUpRequest.getAvailability() : "Mon-Fri 09:00 - 17:00")
                    .build();
            doctorRepository.save(doctor);
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
